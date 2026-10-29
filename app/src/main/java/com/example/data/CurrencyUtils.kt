package com.example.data

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Currency
import java.util.Locale

data class CountryCurrency(
  val countryCode: String,
  val countryName: String,
  val flag: String,
  val currencyCode: String,
  val symbol: String,
  val label: String
) {
  val flagEmoji: String
    get() = flag

  val currencyName: String
    get() = label

  val fullDisplay: String
    get() = "$flag $currencyCode ($symbol) - $countryName"

  val shortDisplay: String
    get() = "$flag $currencyCode ($symbol)"
}

object CurrencyRegistry {

  val WORLD_CURRENCIES = listOf(
    CountryCurrency("KW", "Kuwait", "🇰🇼", "KWD", "K.D.", "🇰🇼 KWD (K.D.) - Kuwait"),
    CountryCurrency("SA", "Saudi Arabia", "🇸🇦", "SAR", "﷼", "🇸🇦 SAR (﷼) - Saudi Arabia"),
    CountryCurrency("AE", "United Arab Emirates", "🇦🇪", "AED", "د.إ", "🇦🇪 AED (د.إ) - UAE"),
    CountryCurrency("QA", "Qatar", "🇶🇦", "QAR", "﷼", "🇶🇦 QAR (﷼) - Qatar"),
    CountryCurrency("BH", "Bahrain", "🇧🇭", "BHD", "BD", "🇧🇭 BHD (BD) - Bahrain"),
    CountryCurrency("OM", "Oman", "🇴🇲", "OMR", "﷼", "🇴🇲 OMR (﷼) - Oman"),
    CountryCurrency("US", "United States", "🇺🇸", "USD", "$", "🇺🇸 USD ($) - United States"),
    CountryCurrency("GB", "United Kingdom", "🇬🇧", "GBP", "£", "🇬🇧 GBP (£) - United Kingdom"),
    CountryCurrency("EU", "European Union", "🇪🇺", "EUR", "€", "🇪🇺 EUR (€) - Europe"),
    CountryCurrency("NP", "Nepal", "🇳🇵", "NPR", "रू", "🇳🇵 NPR (रू) - Nepal"),
    CountryCurrency("IN", "India", "🇮🇳", "INR", "₹", "🇮🇳 INR (₹) - India"),
    CountryCurrency("CA", "Canada", "🇨🇦", "CAD", "$", "🇨🇦 CAD ($) - Canada"),
    CountryCurrency("AU", "Australia", "🇦🇺", "AUD", "$", "🇦🇺 AUD ($) - Australia"),
    CountryCurrency("JP", "Japan", "🇯🇵", "JPY", "¥", "🇯🇵 JPY (¥) - Japan"),
    CountryCurrency("CH", "Switzerland", "🇨🇭", "CHF", "Fr", "🇨🇭 CHF (Fr) - Switzerland"),
    CountryCurrency("SG", "Singapore", "🇸🇬", "SGD", "$", "🇸🇬 SGD ($) - Singapore"),
    CountryCurrency("MY", "Malaysia", "🇲🇾", "MYR", "RM", "🇲🇾 MYR (RM) - Malaysia"),
    CountryCurrency("PH", "Philippines", "🇵🇭", "PHP", "₱", "🇵🇭 PHP (₱) - Philippines"),
    CountryCurrency("PK", "Pakistan", "🇵🇰", "PKR", "₨", "🇵🇰 PKR (₨) - Pakistan"),
    CountryCurrency("BD", "Bangladesh", "🇧🇩", "BDT", "৳", "🇧🇩 BDT (৳) - Bangladesh"),
    CountryCurrency("LK", "Sri Lanka", "🇱🇰", "LKR", "Rs", "🇱🇰 LKR (Rs) - Sri Lanka"),
    CountryCurrency("TR", "Turkey", "🇹🇷", "TRY", "₺", "🇹🇷 TRY (₺) - Turkey"),
    CountryCurrency("BR", "Brazil", "🇧🇷", "BRL", "R$", "🇧🇷 BRL (R$) - Brazil"),
    CountryCurrency("ZA", "South Africa", "🇿🇦", "ZAR", "R", "🇿🇦 ZAR (R) - South Africa"),
    CountryCurrency("MX", "Mexico", "🇲🇽", "MXN", "$", "🇲🇽 MXN ($) - Mexico"),
    CountryCurrency("NZ", "New Zealand", "🇳🇿", "NZD", "$", "🇳🇿 NZD ($) - New Zealand"),
    CountryCurrency("EG", "Egypt", "🇪🇬", "EGP", "E£", "🇪🇬 EGP (E£) - Egypt"),
    CountryCurrency("JO", "Jordan", "🇯🇴", "JOD", "JD", "🇯🇴 JOD (JD) - Jordan"),
    CountryCurrency("IQ", "Iraq", "🇮🇶", "IQD", "ID", "🇮🇶 IQD (ID) - Iraq"),
    CountryCurrency("LB", "Lebanon", "🇱🇧", "LBP", "LL", "🇱🇧 LBP (LL) - Lebanon"),
    CountryCurrency("KR", "South Korea", "🇰🇷", "KRW", "₩", "🇰🇷 KRW (₩) - South Korea"),
    CountryCurrency("ID", "Indonesia", "🇮🇩", "IDR", "Rp", "🇮🇩 IDR (Rp) - Indonesia"),
    CountryCurrency("TH", "Thailand", "🇹🇭", "THB", "฿", "🇹🇭 THB (฿) - Thailand"),
    CountryCurrency("VN", "Vietnam", "🇻🇳", "VND", "₫", "🇻🇳 VND (₫) - Vietnam"),
    CountryCurrency("NG", "Nigeria", "🇳🇬", "NGN", "₦", "🇳🇬 NGN (₦) - Nigeria"),
    CountryCurrency("KE", "Kenya", "🇰🇪", "KES", "KSh", "🇰🇪 KES (KSh) - Kenya"),
    CountryCurrency("GH", "Ghana", "🇬🇭", "GHS", "GH₵", "🇬🇭 GHS (GH₵) - Ghana"),
    CountryCurrency("RU", "Russia", "🇷🇺", "RUB", "₽", "🇷🇺 RUB (₽) - Russia"),
    CountryCurrency("CN", "China", "🇨🇳", "CNY", "¥", "🇨🇳 CNY (¥) - China"),
    CountryCurrency("HK", "Hong Kong", "🇭🇰", "HKD", "HK$", "🇭🇰 HKD (HK$) - Hong Kong"),
    CountryCurrency("TW", "Taiwan", "🇹🇼", "TWD", "NT$", "🇹🇼 TWD (NT$) - Taiwan")
  )

  fun detectDeviceCountryCurrency(context: Context?): CountryCurrency {
    var detectedCountry: String? = null

    // 1. Try TelephonyManager (SIM or Network Country)
    if (context != null) {
      try {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val simCountry = tm?.simCountryIso?.uppercase(Locale.ROOT)?.trim()
        val networkCountry = tm?.networkCountryIso?.uppercase(Locale.ROOT)?.trim()
        if (!simCountry.isNullOrBlank() && simCountry.length == 2) {
          detectedCountry = simCountry
        } else if (!networkCountry.isNullOrBlank() && networkCountry.length == 2) {
          detectedCountry = networkCountry
        }
      } catch (_: Exception) {}
    }

    // 2. Try System Default Locale
    if (detectedCountry.isNullOrBlank()) {
      val defaultLocale = Locale.getDefault()
      val localeCountry = defaultLocale.country?.uppercase(Locale.ROOT)?.trim()
      if (!localeCountry.isNullOrBlank() && localeCountry.length == 2) {
        detectedCountry = localeCountry
      }
    }

    // 3. Match against WORLD_CURRENCIES
    if (!detectedCountry.isNullOrBlank()) {
      val match = WORLD_CURRENCIES.find { it.countryCode.equals(detectedCountry, ignoreCase = true) }
      if (match != null) return match
    }

    // 4. Try Java Currency for the default locale
    try {
      val defaultLocale = Locale.getDefault()
      val javaCurr = Currency.getInstance(defaultLocale)
      if (javaCurr != null) {
        val code = javaCurr.currencyCode
        val symbol = javaCurr.getSymbol(defaultLocale)
        val country = defaultLocale.country ?: "KW"
        val flag = countryCodeToFlag(country)
        val name = defaultLocale.displayCountry.ifBlank { "Local" }
        return CountryCurrency(
          countryCode = country,
          countryName = name,
          flag = flag,
          currencyCode = code,
          symbol = symbol,
          label = "$flag $code ($symbol) - $name"
        )
      }
    } catch (_: Exception) {}

    // Default to Kuwait KWD (K.D.) as original delivery driver default
    return WORLD_CURRENCIES.first()
  }

  fun resolveCurrency(rawCurrency: String): CountryCurrency {
    val clean = rawCurrency.trim()
    if (clean.isBlank()) return WORLD_CURRENCIES.first()

    // Match exact short/full label or code or symbol
    val match = WORLD_CURRENCIES.find { item ->
      item.label.equals(clean, ignoreCase = true) ||
      item.shortDisplay.equals(clean, ignoreCase = true) ||
      item.currencyCode.equals(clean, ignoreCase = true) ||
      item.symbol.equals(clean, ignoreCase = true) ||
      clean.contains(item.currencyCode, ignoreCase = true) ||
      clean.contains(item.symbol, ignoreCase = true) ||
      (clean.contains("KD", ignoreCase = true) && item.currencyCode == "KWD") ||
      (clean.contains("K.D", ignoreCase = true) && item.currencyCode == "KWD") ||
      (clean.contains("NPR", ignoreCase = true) && item.currencyCode == "NPR") ||
      (clean.contains("Rs", ignoreCase = true) && item.currencyCode == "NPR")
    }

    return match ?: CountryCurrency(
      countryCode = "KW",
      countryName = "Custom",
      flag = "🌐",
      currencyCode = clean,
      symbol = clean,
      label = "🌐 $clean"
    )
  }

  fun countryCodeToFlag(countryCode: String): String {
    if (countryCode.length != 2) return "🌐"
    val firstChar = Character.codePointAt(countryCode.uppercase(Locale.ROOT), 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(countryCode.uppercase(Locale.ROOT), 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
  }
}
