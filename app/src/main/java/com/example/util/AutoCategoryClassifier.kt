package com.example.util

data class AutoCategoryResult(
    val categoryKey: String,
    val nameEn: String,
    val nameTe: String,
    val matchedKeyword: String,
    val iconName: String,
    val colorHex: String
)

object AutoCategoryClassifier {

    private val categories = listOf(
        AutoCategoryResult(
            categoryKey = "hospital",
            nameEn = "Hospital & Health",
            nameTe = "ఆసుపత్రి & ఆరోగ్యం",
            matchedKeyword = "",
            iconName = "LocalHospital",
            colorHex = "#EF4444"
        ),
        AutoCategoryResult(
            categoryKey = "food",
            nameEn = "Food & Dining",
            nameTe = "ఆహారం & భోజనం",
            matchedKeyword = "",
            iconName = "Restaurant",
            colorHex = "#F59E0B"
        ),
        AutoCategoryResult(
            categoryKey = "travel",
            nameEn = "Travel & Fuel",
            nameTe = "ప్రయాణం & ఇంధనం",
            matchedKeyword = "",
            iconName = "DirectionsCar",
            colorHex = "#3B82F6"
        ),
        AutoCategoryResult(
            categoryKey = "bills",
            nameEn = "Bills & Utilities",
            nameTe = "బిల్లులు & యుటిలిటీస్",
            matchedKeyword = "",
            iconName = "ReceiptLong",
            colorHex = "#8B5CF6"
        ),
        AutoCategoryResult(
            categoryKey = "shopping",
            nameEn = "Shopping",
            nameTe = "షాపింగ్",
            matchedKeyword = "",
            iconName = "ShoppingBag",
            colorHex = "#EC4899"
        ),
        AutoCategoryResult(
            categoryKey = "education",
            nameEn = "Education",
            nameTe = "విద్య",
            matchedKeyword = "",
            iconName = "School",
            colorHex = "#10B981"
        ),
        AutoCategoryResult(
            categoryKey = "entertainment",
            nameEn = "Entertainment",
            nameTe = "వినోదం",
            matchedKeyword = "",
            iconName = "Movie",
            colorHex = "#6366F1"
        ),
        AutoCategoryResult(
            categoryKey = "personal",
            nameEn = "Personal",
            nameTe = "వ్యక్తిగతం",
            matchedKeyword = "",
            iconName = "Person",
            colorHex = "#14B8A6"
        )
    )

    private val keywordRules = mapOf(
        "hospital" to listOf(
            "apollo", "hospital", "pharmacy", "clinic", "doctor", "dr.", "dr ", "medplus", "medicine",
            "medical", "health", "care", "yashoda", "kims", "dentist", "eye", "tablet", "ayurveda",
            "scan", "lab", "diagnostics", "nurse", "surgery", "therapy", "వైద్యం", "ఆసుపత్రి", "మందులు"
        ),
        "food" to listOf(
            "swiggy", "zomato", "restaurant", "hotel", "biryani", "food", "cafe", "tea", "chai",
            "tiffin", "mess", "bakery", "dosa", "idli", "curry", "pizza", "burger", "dhaba", "sweets",
            "bawarchi", "paradise", "kitchen", "lunch", "dinner", "breakfast", "భోజనం", "ఆహారం", "హోటల్", "టిఫిన్"
        ),
        "travel" to listOf(
            "petrol", "diesel", "fuel", "indianoil", "hpcl", "bpcl", "shell", "uber", "ola", "rapido",
            "auto", "metro", "bus", "train", "irctc", "flight", "toll", "fastag", "parking", "cab",
            "railway", "indigo", "airindia", "ప్రయాణం", "పెట్రోల్", "డీజిల్"
        ),
        "bills" to listOf(
            "electricity", "power", "tsspdcl", "apcpdcl", "bescom", "water", "wifi", "broadband",
            "airtel", "jio", "vi", "bsnl", "gas", "indane", "cylinder", "dth", "tatasky", "recharge",
            "bill", "maintenance", "బిల్లు", "కరెంట్", "గ్యాస్", "రీఛార్జ్"
        ),
        "shopping" to listOf(
            "amazon", "flipkart", "myntra", "dmart", "reliance", "supermarket", "mart", "store",
            "kirana", "cloth", "textiles", "trends", "zudio", "grocery", "mall", "shoppers",
            "jewellery", "footwear", "షాపింగ్", "కిరాణా", "మార్ట్"
        ),
        "education" to listOf(
            "school", "college", "tuition", "books", "academy", "university", "fees", "course",
            "exam", "student", "class", "udemy", "coursera", "chaitanya", "narayana", "విద్య", "పుస్తకాలు", "ఫీజు"
        ),
        "entertainment" to listOf(
            "pvr", "inox", "cinepolis", "movie", "cinema", "theatre", "netflix", "prime", "hotstar",
            "game", "concert", "event", "bookmyshow", "ticket", "సినిమా", "ఆటలు"
        ),
        "personal" to listOf(
            "rent", "friend", "mom", "dad", "amma", "nanna", "brother", "sister", "family",
            "gift", "loan", "salary", "self", "personal", "అద్దె", "స్నేహితుడు", "వ్యక్తిగతం"
        )
    )

    fun classify(payeeName: String, upiId: String, note: String): AutoCategoryResult? {
        val combinedText = "$payeeName $upiId $note".lowercase()
        if (combinedText.isBlank()) return null

        for ((catKey, keywords) in keywordRules) {
            for (keyword in keywords) {
                if (combinedText.contains(keyword)) {
                    val cat = categories.firstOrNull { it.categoryKey == catKey }
                    if (cat != null) {
                        return cat.copy(matchedKeyword = keyword)
                    }
                }
            }
        }
        return null
    }

    fun getAllDefaultCategories(): List<AutoCategoryResult> = categories

    fun getCategoryByKey(key: String): AutoCategoryResult {
        return categories.firstOrNull { it.categoryKey == key } ?: categories.last()
    }
}
