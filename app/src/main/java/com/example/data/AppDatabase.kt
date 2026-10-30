package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface FarmDao {
    @Query("SELECT * FROM crop_profile WHERE id = 1 LIMIT 1")
    fun getCropProfile(): Flow<CropProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: CropProfile)

    @Query("SELECT * FROM crop_observations ORDER BY dayNumber DESC")
    fun getAllObservations(): Flow<List<CropObservation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservation(observation: CropObservation)

    @Query("SELECT * FROM farm_tasks ORDER BY stageIndex ASC, id ASC")
    fun getAllTasks(): Flow<List<FarmTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: FarmTask)

    @Update
    suspend fun updateTask(task: FarmTask)

    @Query("SELECT * FROM market_options ORDER BY distanceKm ASC")
    fun getAllMarkets(): Flow<List<MarketOption>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarkets(markets: List<MarketOption>)

    @Query("UPDATE crop_profile SET healthScore = :score WHERE id = 1")
    suspend fun updateHealthScore(score: Int)

    // Farmer Profile Queries
    @Query("SELECT * FROM farmers WHERE id = :id LIMIT 1")
    fun getFarmer(id: String): Flow<Farmer?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: Farmer)

    // Farm Parcel Queries
    @Query("SELECT * FROM farms WHERE farmerId = :farmerId")
    fun getAllFarms(farmerId: String): Flow<List<Farm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: Farm)

    @Update
    suspend fun updateFarm(farm: Farm)

    // Session State
    @Query("SELECT * FROM session_state WHERE id = 1 LIMIT 1")
    fun getSession(): Flow<SessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    // Risk Alerts
    @Query("SELECT * FROM risk_alerts ORDER BY id DESC")
    fun getAllRisks(): Flow<List<RiskAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRisk(risk: RiskAlert)

    @Update
    suspend fun updateRisk(risk: RiskAlert)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Update
    suspend fun updateNotification(notification: NotificationItem)
}

@Database(
    entities = [
        CropProfile::class,
        CropObservation::class,
        FarmTask::class,
        MarketOption::class,
        Farmer::class,
        Farm::class,
        CropCycle::class,
        RiskAlert::class,
        NotificationItem::class,
        SessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kisan_journey.db"
                ).addCallback(AppDatabaseCallback(scope))
                 .fallbackToDestructiveMigration(dropAllTables = true)
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.farmDao())
                }
            }
        }

        suspend fun populateInitialData(dao: FarmDao) {
            // Initial Session (Logged out by default to trigger Onboarding & Setup)
            dao.insertSession(
                SessionEntity(
                    id = 1,
                    sessionKey = "KSN-NEW-IN",
                    activeFarmerId = "",
                    activeFarmId = "",
                    isLoggedIn = false
                )
            )

            // Initial Farmer
            dao.insertFarmer(
                Farmer(
                    id = "farmer_demo_1",
                    name = "Murugan (Farmer)",
                    phone = "+91 98765 43210",
                    preferredLanguage = "English",
                    location = "Coimbatore, Tamil Nadu",
                    profileCompletionPercent = 90
                )
            )

            // Initial Farm
            dao.insertFarm(
                Farm(
                    id = "farm_demo_1",
                    farmerId = "farmer_demo_1",
                    farmName = "Coimbatore Green Valley Farm",
                    location = "Coimbatore, Tamil Nadu",
                    landSizeAcres = 2.0,
                    soilType = "Red Sandy Loam (pH 6.8)",
                    waterAvailability = "Borewell + Drip Irrigation",
                    coordinates = "11.0168° N, 76.9558° E",
                    isDefault = true
                )
            )

            // Initial Crop Profile
            dao.insertOrUpdateProfile(
                CropProfile(
                    id = 1,
                    cropName = "Tomato",
                    variety = "Shivam / Pusa Ruby",
                    acres = 2.0,
                    location = "Coimbatore, Tamil Nadu",
                    plantingDate = "Aug 25, 2026",
                    daysAfterTransplant = 34,
                    currentStage = "02 GROW (Vegetative)",
                    healthScore = 86,
                    previousHealthScore = 86,
                    soilType = "Red Sandy Loam",
                    irrigationType = "Drip Irrigation"
                )
            )

            // Initial Observations
            dao.insertObservation(
                CropObservation(
                    dateLabel = "Day 28 • Previous Observation",
                    stage = "Vegetative Stage",
                    healthScore = 86,
                    summary = "Optimal vegetative growth with clean dark green leaves.",
                    detail = "No pest spotting. Branching active. Stem thickness optimal. Moisture distribution steady.",
                    riskLevel = "Low",
                    isToday = false,
                    leafCondition = "Healthy green leaf lamina, crisp margins, no spotting.",
                    weatherCondition = "Sunny & Warm"
                )
            )

            // Initial Tasks
            val initialTasks = listOf(
                FarmTask(
                    stageIndex = 1,
                    title = "Soil nutrient and solarization check",
                    timing = "Before planting",
                    guidance = "Incorporate 10 tonnes well-decomposed FYM per acre before bed preparation.",
                    isCompleted = true,
                    category = "Soil Health"
                ),
                FarmTask(
                    stageIndex = 1,
                    title = "Drip lateral layout and bed mulching",
                    timing = "Before planting",
                    guidance = "Install 16mm inline drip lines with 40cm emitter spacing.",
                    isCompleted = true,
                    category = "Irrigation"
                ),
                FarmTask(
                    stageIndex = 2,
                    title = "Panchagavya organic foliar spray",
                    timing = "Every 10 days",
                    guidance = "Mix 30ml Panchagavya per liter of water. Spray early morning before sun intensifies.",
                    isCompleted = false,
                    category = "Nutrient"
                ),
                FarmTask(
                    stageIndex = 2,
                    title = "Bamboo staking & trellising check",
                    timing = "Day 35",
                    guidance = "Tie growing tomato vines gently with jute twine to support vertical growth and airflow.",
                    isCompleted = false,
                    category = "Canopy"
                ),
                FarmTask(
                    stageIndex = 3,
                    title = "Foliar health photo scan",
                    timing = "Today",
                    guidance = "Inspect middle and lower leaves for moisture discoloration after high humidity night.",
                    isCompleted = false,
                    category = "Monitoring"
                ),
                FarmTask(
                    stageIndex = 4,
                    title = "Neem seed kernel extract (NSKE 5%) preventative spray",
                    timing = "Recommended tomorrow",
                    guidance = "Spray lower canopy undersides to discourage early blight spores during humid weather.",
                    isCompleted = false,
                    category = "Protection"
                ),
                FarmTask(
                    stageIndex = 5,
                    title = "Crate inventory & harvesting labor prep",
                    timing = "In 3 weeks",
                    guidance = "Prepare 150 sanitized plastic crates for harvest at breaker color stage.",
                    isCompleted = false,
                    category = "Yield Prep"
                ),
                FarmTask(
                    stageIndex = 6,
                    title = "Uzhavar Sandhai direct market slot booking",
                    timing = "Before harvest",
                    guidance = "Check daily retail quotes and reserve stall space for early morning dispatch.",
                    isCompleted = false,
                    category = "Market"
                )
            )
            initialTasks.forEach { dao.insertTask(it) }

            // Initial Markets
            val markets = listOf(
                MarketOption(
                    marketName = "Coimbatore Uzhavar Sandhai",
                    distanceKm = 12,
                    pricePerKg = 36,
                    demandLevel = "High",
                    transportCost = "Low (₹1.20/kg)",
                    transportCostNum = 1.20,
                    marketType = "Farmer Direct Market",
                    suitabilityBadge = "Most Profitable",
                    recommendationNote = "Direct sale to consumers with zero middlemen commissions. Best net return."
                ),
                MarketOption(
                    marketName = "Mettupalayam Wholesale Mandi",
                    distanceKm = 28,
                    pricePerKg = 32,
                    demandLevel = "Very High",
                    transportCost = "Medium (₹2.40/kg)",
                    transportCostNum = 2.40,
                    marketType = "Wholesale Mandi",
                    suitabilityBadge = "Bulk Offtake",
                    recommendationNote = "Ideal for rapid clearance of entire 5+ tonne harvest within 2 hours."
                ),
                MarketOption(
                    marketName = "Pollachi APMC Market Yard",
                    distanceKm = 42,
                    pricePerKg = 38,
                    demandLevel = "High",
                    transportCost = "Higher (₹3.60/kg)",
                    transportCostNum = 3.60,
                    marketType = "APMC Yard",
                    suitabilityBadge = "Highest Spot Price",
                    recommendationNote = "Highest gross rate, but factor in 42km transport fuel and 1.5% APMC market cess."
                ),
                MarketOption(
                    marketName = "Local Farmgate Aggregator",
                    distanceKm = 0,
                    pricePerKg = 29,
                    demandLevel = "Moderate",
                    transportCost = "None (₹0.00)",
                    transportCostNum = 0.0,
                    marketType = "Farmgate Pickup",
                    suitabilityBadge = "Zero Effort",
                    recommendationNote = "Trader collects directly at your field. Zero transport effort, but lower per-kg rate."
                )
            )
            dao.insertMarkets(markets)
        }
    }
}
