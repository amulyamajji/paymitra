package com.example.util

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    TELUGU("te", "Telugu", "తెలుగు")
}

object AppStrings {
    fun get(key: String, lang: AppLanguage): String {
        val en = stringsEn[key] ?: key
        if (lang == AppLanguage.ENGLISH) return en
        return stringsTe[key] ?: en
    }

    private val stringsEn = mapOf(
        "app_title" to "PayMitra UPI",
        "home" to "Home",
        "pay" to "Pay",
        "dashboard" to "Analytics",
        "history" to "History",
        "sos" to "SOS Emergency",
        "voice_guide" to "Audio Guide",
        
        // Home Header & Balance
        "greeting" to "Namaste, Ramesh",
        "upi_id_label" to "UPI ID",
        "primary_bank" to "State Bank of India (Primary)",
        "account_balance" to "Account Balance",
        "check_balance" to "Check Balance",
        "hide_balance" to "Hide",
        "show_balance" to "Show",
        "read_balance" to "Read Balance Aloud",
        
        // Quick Actions
        "scan_qr" to "Scan QR",
        "to_mobile" to "To Mobile",
        "to_upi" to "To UPI ID",
        "bank_transfer" to "Bank Transfer",
        "voice_pay" to "Voice Pay",
        "quick_sos" to "Quick SOS",
        
        // Budget & Spend
        "monthly_budget" to "Monthly Budget",
        "budget_limit" to "Budget Limit",
        "total_spent" to "Spent This Month",
        "remaining_budget" to "Remaining Budget",
        "budget_warning" to "Approaching monthly limit!",
        "budget_safe" to "Budget on track",
        "budget_exceeded" to "Budget exceeded!",
        
        // Pay Screen
        "send_money" to "Send Money",
        "recipient" to "Recipient Details",
        "enter_upi_id" to "Enter UPI ID / Mobile",
        "amount_inr" to "Amount (₹)",
        "purpose_category" to "Purpose & Category",
        "auto_detected" to "Auto-detected Purpose",
        "manual_category" to "Expense Category",
        "add_note" to "Add a note / reason (Optional)",
        "transaction_location" to "Transaction Location",
        "detect_location" to "Using Current Location",
        "change_location" to "Change",
        "pay_now" to "Pay Now",
        "enter_upi_pin" to "Enter UPI PIN",
        "payment_successful" to "Payment Successful",
        "payment_failed" to "Payment Failed",
        "upi_ref_no" to "UPI Reference No",
        
        // Categories
        "cat_food" to "Food & Dining",
        "cat_hospital" to "Hospital & Health",
        "cat_personal" to "Personal",
        "cat_travel" to "Travel & Fuel",
        "cat_bills" to "Bills & Utilities",
        "cat_shopping" to "Shopping",
        "cat_education" to "Education",
        "cat_entertainment" to "Entertainment",
        "cat_custom" to "Custom Category",
        "cat_all" to "All Categories",
        "add_custom_category" to "+ Add Custom Category",
        "custom_category_name" to "Custom Category Name",
        "save_category" to "Save Category",
        
        // Dashboard / Graph
        "spending_analytics" to "Monthly Spending Habit",
        "spending_trends" to "Financial Trends & Insights",
        "category_breakdown" to "Category-wise Breakdown",
        "daily_expenditure_graph" to "Daily Spending Graph",
        "month_comparison" to "Monthly Comparison",
        "top_spending_category" to "Top Spending Category",
        "avg_daily_spend" to "Avg Daily Expense",
        
        // History
        "transaction_history" to "Transaction History",
        "search_transactions" to "Search payee, category, location...",
        "all" to "All",
        "debit" to "Paid (Debit)",
        "credit" to "Received (Credit)",
        "view_receipt" to "View Digital Receipt",
        "share_receipt" to "Share Receipt",
        "repeat_payment" to "Repeat",
        
        // Fingerprint Lock
        "fingerprint_title" to "PayMitra Security",
        "fingerprint_desc" to "Touch sensor or verify to unlock",
        "fingerprint_hint" to "Use Fingerprint to Unlock",
        "use_upi_pin" to "Use UPI PIN (1234)",
        "auth_success" to "Authentication Successful",
        "privacy_mode" to "Fingerprint Lock",
        
        // Emergency SOS
        "emergency_sos" to "Critical Emergency SOS",
        "sos_desc" to "Rapid panic alerts, live location dispatch and instant medical fund",
        "panic_alert" to "PRESS FOR PANIC ALERT",
        "alert_triggered" to "SOS Alert Activated!",
        "current_coordinates" to "Current GPS Coordinates",
        "sos_contacts" to "Emergency SOS Contacts",
        "send_sos_sms" to "Send SOS Distress Alert",
        "emergency_speed_dial" to "Emergency Speed Dial",
        "ambulance" to "Ambulance (108)",
        "police" to "Police (100)",
        "women_helpline" to "Women Helpline (1091)",
        "instant_medical_fund" to "Instant Emergency UPI Reserve (₹2,000)",
        "add_contact" to "Add Emergency Contact",
        
        // Audio Guide & Accessibility for Illiterate Users
        "audio_guide_mode" to "Audio Assistance Mode",
        "audio_guide_desc" to "Spoken instructions & voice commands for easy understanding",
        "voice_command" to "Speak Voice Command",
        "listening" to "Listening... Speak now",
        "voice_hint" to "e.g., 'Pay 500 for food' or 'Check balance'",
        "tap_to_listen" to "Tap Speaker to Hear This Screen"
    )

    private val stringsTe = mapOf(
        "app_title" to "పేమిత్ర UPI",
        "home" to "హోమ్",
        "pay" to "చెల్లింపు",
        "dashboard" to "విశ్లేషణ",
        "history" to "చరిత్ర",
        "sos" to "అత్యవసర SOS",
        "voice_guide" to "వాయిస్ గైడ్",
        
        // Home Header & Balance
        "greeting" to "నమస్తే, రమేష్",
        "upi_id_label" to "UPI ఐడీ",
        "primary_bank" to "స్టేట్ బ్యాంక్ ఆఫ్ ఇండియా (ప్రైమరీ)",
        "account_balance" to "ఖాతా నిల్వ (బ్యాలెన్స్)",
        "check_balance" to "బ్యాలెన్స్ తనిఖీ",
        "hide_balance" to "దాచు",
        "show_balance" to "చూపించు",
        "read_balance" to "బ్యాలెన్స్ చదవండి",
        
        // Quick Actions
        "scan_qr" to "QR స్కాన్",
        "to_mobile" to "మొబైల్‌కి",
        "to_upi" to "UPI ఐడీకి",
        "bank_transfer" to "బ్యాంక్ బదిలీ",
        "voice_pay" to "వాయిస్ పే",
        "quick_sos" to "త్వరిత SOS",
        
        // Budget & Spend
        "monthly_budget" to "నెలవారీ బడ్జెట్",
        "budget_limit" to "బడ్జెట్ పరిమితి",
        "total_spent" to "ఈ నెల ఖర్చు",
        "remaining_budget" to "మిగిలిన బడ్జెట్",
        "budget_warning" to "బడ్జెట్ పరిమితి దగ్గరపడుతోంది!",
        "budget_safe" to "బడ్జెట్ సరైన మార్గంలో ఉంది",
        "budget_exceeded" to "బడ్జెట్ పరిమితి దాటింది!",
        
        // Pay Screen
        "send_money" to "డబ్బులు పంపండి",
        "recipient" to "గ్రహీత వివరాలు",
        "enter_upi_id" to "UPI ఐడీ లేదా మొబైల్ నంబర్",
        "amount_inr" to "మొత్తం (₹)",
        "purpose_category" to "లావాదేవీ ఉద్దేశం & వర్గం",
        "auto_detected" to "స్వయంచాలకంగా గుర్తించిన వర్గం",
        "manual_category" to "ఖర్చు వర్గం",
        "add_note" to "నోట్ / కారణం రాయండి (ఐచ్ఛికం)",
        "transaction_location" to "లావాదేవీ జరిగిన ప్రదేశం",
        "detect_location" to "ప్రస్తుత ప్రదేశం ఉపయోగించబడుతోంది",
        "change_location" to "మార్చు",
        "pay_now" to "ఇప్పుడే చెల్లించండి",
        "enter_upi_pin" to "UPI పిన్ నమోదు చేయండి",
        "payment_successful" to "చెల్లింపు విజయవంతమైంది",
        "payment_failed" to "చెల్లింపు విఫలమైంది",
        "upi_ref_no" to "UPI రిఫరెన్స్ సంఖ్య",
        
        // Categories
        "cat_food" to "ఆహారం & భోజనం",
        "cat_hospital" to "ఆసుపత్రి & ఆరోగ్యం",
        "cat_personal" to "వ్యక్తిగతం",
        "cat_travel" to "ప్రయాణం & ఇంధనం",
        "cat_bills" to "బిల్లులు & యుటిలిటీస్",
        "cat_shopping" to "షాపింగ్",
        "cat_education" to "విద్య",
        "cat_entertainment" to "వినోదం",
        "cat_custom" to "అనుకూల వర్గం",
        "cat_all" to "అన్ని వర్గాలు",
        "add_custom_category" to "+ కొత్త వర్గాన్ని జోడించు",
        "custom_category_name" to "వర్గం పేరు",
        "save_category" to "వర్గాన్ని భద్రపరుచు",
        
        // Dashboard / Graph
        "spending_analytics" to "నెలవారీ ఖర్చుల అలవాట్లు",
        "spending_trends" to "ఆర్థిక పోకడలు & విశ్లేషణ",
        "category_breakdown" to "వర్గాల వారీగా ఖర్చుల విభజన",
        "daily_expenditure_graph" to "రోజువారీ ఖర్చుల గ్రాఫ్",
        "month_comparison" to "నెలవారీ పోలిక",
        "top_spending_category" to "అత్యధిక ఖర్చు వర్గం",
        "avg_daily_spend" to "సగటు రోజువారీ ఖర్చు",
        
        // History
        "transaction_history" to "లావాదేవీల చరిత్ర",
        "search_transactions" to "పేరు, వర్గం, ప్రదేశం ద్వారా వెతకండి...",
        "all" to "అన్నీ",
        "debit" to "చెల్లించినవి (డెబిట్)",
        "credit" to "వచ్చినవి (క్రెడిట్)",
        "view_receipt" to "డిజిటల్ రసీదు చూడండి",
        "share_receipt" to "రసీదు షేర్ చేయండి",
        "repeat_payment" to "మళ్ళీ పంపండి",
        
        // Fingerprint Lock
        "fingerprint_title" to "పేమిత్ర భద్రతా లాక్",
        "fingerprint_desc" to "అన్‌లాక్ చేయడానికి వేలిముద్ర సెన్సార్ తాకండి",
        "fingerprint_hint" to "వేలిముద్రతో అన్‌లాక్ చేయండి",
        "use_upi_pin" to "UPI పిన్ ఉపయోగించండి (1234)",
        "auth_success" to "భద్రతా ధృవీకరణ పూర్తయింది",
        "privacy_mode" to "వేలిముద్ర లాక్",
        
        // Emergency SOS
        "emergency_sos" to "అత్యవసర SOS సహాయం",
        "sos_desc" to "త్వరిత ప్రమాద హెచ్చరిక, లైవ్ లొకేషన్ మరియు తక్షణ వైద్య నిధి",
        "panic_alert" to "ప్రమాద హెచ్చరిక కోసం ఇక్కడ నొక్కండి",
        "alert_triggered" to "SOS హెచ్చరిక ప్రారంభమైంది!",
        "current_coordinates" to "ప్రస్తుత GPS స్థానం",
        "sos_contacts" to "అత్యవసర సంప్రదింపులు",
        "send_sos_sms" to "SOS సందేశం పంపండి",
        "emergency_speed_dial" to "అత్యవసర డయల్ నంబర్లు",
        "ambulance" to "అంబులెన్స్ (108)",
        "police" to "పోలీస్ (100)",
        "women_helpline" to "మహిళా హెల్ప్‌లైన్ (1091)",
        "instant_medical_fund" to "తక్షణ అత్యవసర వైద్య నిధి (₹2,000)",
        "add_contact" to "కాంటాక్ట్ జోడించండి",
        
        // Audio Guide & Accessibility
        "audio_guide_mode" to "వాయిస్ సహాయ మోడ్",
        "audio_guide_desc" to "చదువురాని వారికోసం ధ్వని సూచనలు మరియు వాయిస్ కమాండ్లు",
        "voice_command" to "వాయిస్ కమాండ్ మాట్లాడండి",
        "listening" to "వినబడుతోంది... ఇప్పుడు మాట్లాడండి",
        "voice_hint" to "ఉదా: 'ఆహారానికి 500 చెల్లించండి' లేదా 'బ్యాలెన్స్ ఎంత'",
        "tap_to_listen" to "ఈ స్క్రీన్‌ను వినడానికి స్పీకర్‌ను తాకండి"
    )
}
