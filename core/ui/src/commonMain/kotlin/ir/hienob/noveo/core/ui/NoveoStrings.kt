package ir.hienob.noveo.core.ui

/**
 * Platform-neutral strings used by the shared root frame.
 * Full screen-level localization can be moved here as additional UI is extracted.
 */
data class NoveoStrings(
    val languageCode: String = "en",
    val brandName: String = "Noveo",
    val next: String = "Next",
    val skip: String = "Skip",
    val onboardingAction: String = "Get Started",
    val onboardingPages: List<String> = listOf(
        "Chat with your contacts in one place.",
        "Jump into conversations quickly with the Noveo mobile shell.",
        "Stay synced and start messaging as soon as you sign in."
    ),
    val loginTitle: String = "Welcome back",
    val signupTitle: String = "Join Noveo",
    val switchSignup: String = "New here? Create an account",
    val switchLogin: String = "Already have an account? Log in",
    val loginButton: String = "Log In",
    val signupButton: String = "Sign Up",
    val handlePlaceholder: String = "Username or Handle",
    val passwordPlaceholder: String = "Password",
    val registerOnWebTitle: String = "Register on Noveo Web",
    val registerOnWebBody: String = "You have to register from Noveo Web for security reasons.",
    val openNoveoWeb: String = "Open Noveo Web"
)

fun coreNoveoStrings(languageCode: String?): NoveoStrings = when (languageCode?.lowercase()) {
    "fa" -> NoveoStrings(
        languageCode = "fa",
        next = "بعدی",
        skip = "رد کردن",
        onboardingAction = "شروع",
        loginTitle = "خوش برگشتی",
        signupTitle = "عضویت در Noveo",
        switchSignup = "تازه واردی؟ حساب بساز",
        switchLogin = "حساب داری؟ وارد شو",
        loginButton = "ورود",
        signupButton = "ثبت نام",
        handlePlaceholder = "نام کاربری یا هندل",
        passwordPlaceholder = "رمز عبور",
        registerOnWebTitle = "ثبت نام در وب Noveo",
        registerOnWebBody = "برای امنیت، ثبت نام باید از Noveo Web انجام شود.",
        openNoveoWeb = "باز کردن Noveo Web",
        onboardingPages = listOf(
            "با مخاطبانت در یک جا گفتگو کن.",
            "با پوسته موبایل Noveo سریع وارد گفتگوها شو.",
            "همگام بمان و بعد از ورود پیام دادن را شروع کن."
        )
    )
    else -> NoveoStrings(languageCode = languageCode ?: "en")
}
