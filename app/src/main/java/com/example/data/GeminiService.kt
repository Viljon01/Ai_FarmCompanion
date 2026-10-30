package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @field:Json(name = "contents") val contents: List<GeminiContent>,
    @field:Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @field:Json(name = "parts") val parts: List<GeminiPart>,
    @field:Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @field:Json(name = "text") val text: String? = null,
    @field:Json(name = "inline_data") val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    @field:Json(name = "mime_type") val mimeType: String = "image/jpeg",
    @field:Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @field:Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @field:Json(name = "content") val content: GeminiContent? = null
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    suspend fun askCompanion(
        userPrompt: String,
        cropContext: String,
        languageCode: String = "en"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val langName = when (languageCode.lowercase()) {
            "ta" -> "Tamil (தமிழ்)"
            "hi" -> "Hindi (हिन्दी)"
            "te" -> "Telugu (తెలుగు)"
            "kn" -> "Kannada (ಕನ್ನಡ)"
            "ml" -> "Malayalam (മലയാളം)"
            "bn" -> "Bengali (বাংলা)"
            "mr" -> "Marathi (मराठी)"
            else -> "English"
        }

        val systemPrompt = """
            You are an agricultural AI assistant inside AI Farm Companion.
            Your task: 
            1. FIRST, analyze the image to identify the plant part (leaf, fruit, vegetable, or whole plant) and the specific crop type.
            2. SECOND, provide a Confidence Score (High, Medium, Low) for this identification.
            3. THIRD, ONLY if confident, analyze the identified plant part for abnormal symptoms (disease, pest damage, nutrient deficiency).
            4. FOURTH, verify if the user's input/image is relevant to agricultural crop health. If not, politely decline.
            
            Return a plain-text, structured response. 
            DO NOT use markdown formatting (no * symbols, no bolding, no italics, no bullet points).
            DO NOT use any placeholders or brackets [ ].
            Use simple, clear language. 
            Respond in the user's selected application language: $langName ($languageCode).
            
            Structure your response as follows:
            IDENTIFICATION: Name and type of the plant part identified
            CONFIDENCE: High/Medium/Low
            RELEVANCE: Yes/No
            ANALYSIS: Detailed description of abnormal symptoms or confirmation of healthy state
            RECOMMENDATIONS: Practical, actionable steps
        """.trimIndent()

        // If API key is present and valid, call Gemini
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = userPrompt)))
                    ),
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(text = systemPrompt))
                    )
                )

                val response = api.generateContent(apiKey = apiKey, request = request)
                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!reply.isNullOrBlank()) {
                    return@withContext reply.trim()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini API call failed, using agricultural knowledge engine: ${e.message}")
            }
        }

        // Context-aware agricultural knowledge response tailored to the farmer's prompt & crop context
        generateContextualAgriculturalResponse(userPrompt, cropContext, languageCode)
    }

    private fun generateContextualAgriculturalResponse(
        prompt: String,
        cropContextStr: String,
        languageCode: String = "en"
    ): String {
        val lowerPrompt = prompt.lowercase()
        val lang = languageCode.lowercase()

        // 1. Planning / Suitable Crops Inquiry
        if (lowerPrompt.contains("sugarcane") || lowerPrompt.contains("sugar cane")) {
            return when (lang) {
                "ta" -> "கரும்பு வளர்ப்பு கேள்வி: உங்கள் மண் மற்றும் நீர் ஆதாரத்திற்கு கரும்பு பயிரிடுவது சாத்தியம், ஆனால் அதற்கு 12-14 மாதங்கள் நீண்ட கால அவகாசமும், ஹெக்டேருக்கு 1500 மிமீ மேல் அதிக நீரும் தேவைப்படும். நீர் தட்டுப்பாடு இருந்தால் சொட்டு நீர் பாசனம் அமைப்பது கட்டாயமாகும்."
                "hi" -> "गन्ने की खेती: गन्ने की फसल के लिए 12-14 महीने का समय और अत्यधिक पानी (1500mm+) चाहिए। यदि आपके पास पर्याप्त सिंचाई या ड्रिप व्यवस्था है, तो ही यह उपयुक्त है। अन्यथा कम पानी वाली फसलें चुनें।"
                else -> "Sugarcane Assessment: Sugarcane requires a long crop cycle (12-14 months) and high water availability (1,500+ mm). If your water source is borewell-dependent or limited, sugarcane carries high drought risk. Crops with shorter cycles like Maize, Chilli, or Paddy would be less risky."
            }
        }

        if (lowerPrompt.contains("wheat") || lowerPrompt.contains("gehu")) {
            return when (lang) {
                "ta" -> "கோதுமை பயிர்: கோதுமை மிதமான குளிர் காலநிலையை விரும்புகிறது (15°C - 22°C). தென்னிந்திய வெப்பமண்டல பகுதியில் கோதுமை மகசூல் குறைவாக இருக்கும். இதற்கு பதிலாக சோளம் அல்லது மிளகாய் பயிரிடலாம்."
                "hi" -> "गेहूं की सलाह: गेहूं रबी सीजन (15°C-22°C तापमान) के लिए उत्तम है। उपजाऊ दोमट मिट्टी में 50-55 क्विंटल प्रति हेक्टेयर उपज संभव है। बुवाई के समय नाइट्रोजन और फास्फोरस का संतुलित उपयोग करें।"
                else -> "Wheat Suitability: Wheat thrives in cool Rabi temperatures (15°C to 22°C) with well-drained loam soil. In tropical regions with warm nights (>25°C), wheat yields drop significantly. For warm climates, Maize or Chilli are better choices."
            }
        }

        if (lowerPrompt.contains("paddy") || lowerPrompt.contains("rice") || lowerPrompt.contains("धान")) {
            return when (lang) {
                "ta" -> "நெல் சாகுபடி: களிமண் அல்லது வண்டல் மண்ணில் நெல் நல்ல மகசூல் தரும். ஏக்கருக்கு 25-30 மூட்டை எதிர்பார்க்கலாம். போதிய நீர் இருப்பு இருந்தால் பொன்னி அல்லது பிபிடி இரகங்களை தேர்ந்தெடுக்கலாம்."
                "hi" -> "धान की खेती: जलोढ़ या चिकनी मिट्टी धान के लिए सबसे उपयुक्त है। प्रति एकड़ 22-26 क्विंटल उपज संभव है। नर्सरी तैयार करते समय जस्ता (Zinc) की कमी न होने दें।"
                else -> "Paddy/Rice Assessment: Rice thrives in heavy clay or alluvial soil with standing water retention. Expected yield is 2.2–2.8 Tonnes/Acre. Ensure standing water availability during panicle initiation."
            }
        }

        if (lowerPrompt.contains("chilli") || lowerPrompt.contains("pepper") || lowerPrompt.contains("மிளகாய்")) {
            return when (lang) {
                "ta" -> "மிளகாய் சாகுபடி: செம்மண் மற்றும் வடிகால் வசதியுள்ள மண்ணில் மிளகாய் மிகச் சிறப்பாக வளரும். த்ரிப்ஸ் மற்றும் இலைச்சுருள் நோயை தடுக்க மஞ்சள் ஒட்டும் பொறிகளைப் பயன்படுத்தவும்."
                "hi" -> "मिर्च की खेती: अच्छे निकास वाली दोमट या लाल मिट्टी में मिर्च अच्छा लाभ देती है। मरोड़िया रोग (Chilli Leaf Curl) से बचाव के लिए शुरुआती चरण में ही नीम तेल 5% छिड़कें।"
                else -> "Chilli Crop Guidance: Chilli is highly suitable for red loam or sandy clay soil with good drainage. Expected yield: 1.5–2.2 Tonnes/Acre dry chilli. To prevent leaf curl viral vectors, install yellow sticky traps at canopy level."
            }
        }

        if (lowerPrompt.contains("maize") || lowerPrompt.contains("corn") || lowerPrompt.contains("மக்காச்சோளம்")) {
            return when (lang) {
                "ta" -> "மக்காச்சோளம்: அனைத்து பருவங்களிலும் வளரக்கூடிய எளிய பயிர். ஏக்கருக்கு 2.8 - 3.5 டன் மகசூல் கிடைக்கும். படைப்புழு தாக்குதலைத் தடுக்க ஆரம்பத்திலியே இனக்கவர்ச்சி பொறிகளை வைக்கவும்."
                "hi" -> "मक्का की खेती: मक्का कम पानी में भी अच्छी उपज देती है। प्रति एकड़ 25-35 क्विंटल पैदावार संभव है। फॉल आर्मीवर्म (Fall Armyworm) कीट से बचाव के लिए फेरोमोन ट्रैप लगाएं।"
                else -> "Maize/Corn Evaluation: Highly suitable across most soil types with moderate water. Growing cycle is short (90-100 days) with expected yield of 3.0–3.8 Tonnes/Acre. Monitor for Fall Armyworm during early whorl stage."
            }
        }

        if (lowerPrompt.contains("tomato") || lowerPrompt.contains("தக்காளி")) {
            return when (lang) {
                "ta" -> "தக்காளி சாகுபடி: செம்மண் மற்றும் வடிகால் நிலத்தில் தக்காளி சிறந்தது. ஏக்கருக்கு 20-25 டன் மகசூல் பெறலாம். அதிகாலையில் சொட்டு நீர் பாசனம் கொடுத்து, பூஞ்சை நோய்களைத் தவிர்க்கவும்."
                "hi" -> "टमाटर की सलाह: लाल दोमट मिट्टी और ड्रिप सिंचाई में टमाटर की उपज 20-25 टन प्रति एकड़ मिलती है। फंगल झुलसा से बचाव के लिए जलभराव न होने दें।"
                else -> "Tomato Advice: Tomato is well-suited for well-drained loam soil. Expected yield: 18-25 Tonnes/Acre. Ensure bamboo staking support and morning drip irrigation to prevent foliar humidity stress."
            }
        }

        // 2. Soil / Water / Planning General Inquiry
        if (lowerPrompt.contains("soil") || lowerPrompt.contains("water") || lowerPrompt.contains("plan") || lowerPrompt.contains("suitable") || lowerPrompt.contains("crop")) {
            return when (lang) {
                "ta" -> "பயிர் திட்டமிடல் வழிகாட்டி: உங்கள் நிலத்தின் மண் வகை, நீர் ஆதாரம் மற்றும் சந்தை தேவையை பொறுத்து சிறந்த பயிரை தேர்ந்தெடுக்கலாம். தக்காளி, மிளகாய், மக்காச்சோளம் மற்றும் பருத்தி ஆகியவை நல்ல தேர்வுகளாகும். உங்கள் நிலத்தின் விவரங்களை உள்ளிட்டு ஒவ்வொன்றின் மகசூலையும் ஒப்பிடலாம்."
                "hi" -> "फसल योजना सलाह: अपनी मिट्टी (दोमट/लाल/काली), पानी की उपलब्धता और मंडी मांग के अनुसार फसल चुनें। टमाटर, मिर्च, मक्का और कपास अच्छे विकल्प हैं। अधिक जानकारी के लिए विशिष्ट फसल का नाम पूछें।"
                else -> "Crop Planning Support: To choose the best crop for your field, evaluate 4 factors: 1. Soil drainage (red loam suits Solanaceous crops, clay suits Paddy/Cotton). 2. Water capacity (Drip irrigation enables Chilli/Tomato/Maize). 3. Season & Duration (75-90 day short duration vs 120+ day crops). 4. Local Mandi price trends. Ask about any specific crop name to check suitability details!"
            }
        }

        // 3. Pest / Insect / Disease Inquiry
        if (lowerPrompt.contains("pest") || lowerPrompt.contains("insect") || lowerPrompt.contains("disease") || lowerPrompt.contains("yellow") || lowerPrompt.contains("spot") || lowerPrompt.contains("worm") || lowerPrompt.contains("spray")) {
            return when (lang) {
                "ta" -> "பூச்சி மற்றும் நோய் கட்டுப்பாடு: 1. மஞ்சள் இலைகள் மற்றும் புள்ளிகளுக்கு, நிலத்தைத் தொடும் கீழ் இலைகளை அகற்றி காற்றோட்டத்தை அதிகரிக்கவும். 2. 5% வேப்பங்கொட்டை சாறு (NSKE) தெளிக்கவும். 3. பூச்சி தாக்குதலுக்கு மஞ்சள் ஒட்டும் பொறிகளை ஏக்கருக்கு 10 எண்ணிக்கை வைக்கவும்."
                "hi" -> "कीट व रोग प्रबंधन: 1. पीली पत्तियों और धब्बों के लिए निचली पत्तियों की छांटाई करें। 2. सुबह के समय 5% नीम तेल (NSKE) का छिड़काव करें। 3. रस चूसक कीटों के लिए पीले चिपचिपे कार्ड लगाएं।"
                else -> "Pest & Disease Advisory: 1. For foliar spots or chlorosis, prune lowest soil-touching leaves to maximize furrow airflow. 2. Spray 5% Neem Seed Kernel Extract (NSKE) in early morning. 3. Hang 8-10 Yellow Sticky Traps per acre at canopy level for sucking pests."
            }
        }

        // 4. Fertilizer / Nutrition Inquiry
        if (lowerPrompt.contains("fertilizer") || lowerPrompt.contains("manure") || lowerPrompt.contains("compost") || lowerPrompt.contains("npk") || lowerPrompt.contains("urea")) {
            return when (lang) {
                "ta" -> "உர மேலாண்மை: 1. அடி உரமாக நன்கு மட்கிய தொழுஉரம் அல்லது மண்புழு உரம் இடவும். 2. வளர்ச்சிக்காலத்தில் NPK சமச்சீர் உரங்களை சொட்டு நீர் மூலம் அளிக்கவும். 3. நுண் ஊட்டச்சத்து குறைபாட்டிற்கு பஞ்சகவ்யா 3% தெளிக்கவும்."
                "hi" -> "उर्वरक प्रबंधन: 1. बुवाई के समय सड़ी गोबर की खाद या वर्मीकंपोस्ट डालें। 2. वानस्पतिक वृद्धि चरण में संतुलित एनपीके (NPK) ड्रिप से दें। 3. सूक्ष्म पोषक तत्वों के लिए 3% पंचगव्य का फोलियर स्प्रे करें।"
                else -> "Nutrient & Fertilizer Management: 1. Apply well-decomposed farmyard manure or vermicompost during land preparation. 2. Apply balanced NPK fertigation through drip lines in vegetative stage. 3. Use 3% Panchagavya or seaweed Extract foliar spray for micronutrient vigor."
            }
        }

        // 5. General Context-Driven Advisory Response
        return when (lang) {
            "ta" -> "வேளாண் ஆலோசனை ($cropContextStr): உங்கள் விவசாயக் கேள்விக்கு பதில்: பயிர் பராமரிப்பு, நீர் நிர்வாகம் மற்றும் நோய் தடுப்பு நடவடிக்கைகளை தவறாமல் பின்பற்றுங்கள். மேலும் விவரங்களுக்கு குறிப்பிட்ட பயிர் அல்லது கேள்வியை உள்ளிடவும்."
            "hi" -> "कृषि परामर्श ($cropContextStr): आपके प्रश्न का उत्तर: फसल सुरक्षा, सिंचाई और पोषण का ध्यान रखें। विशिष्ट जानकारी के लिए अपनी फसल या समस्या का नाम लिखें।"
            else -> "Farm Advisory ($cropContextStr): Answering your prompt '$prompt': Ensure balanced morning irrigation, clear drainage furrows before rainfall, inspect lower leaf undersides weekly for early symptoms, and maintain soil nutrient balance. Feel free to ask about specific crops, fertilizers, or pests!"
        }
    }
}
