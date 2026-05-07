# kotlinx-datetime exposes optional serializers when kotlinx-serialization is present.
# The desktop package does not use those serializers, so keep packaging stable by
# suppressing the optional-reference warnings instead of failing release packaging.
-dontwarn kotlinx.serialization.**
-dontwarn kotlinx.datetime.serializers.**
-dontwarn sun.font.CFont
