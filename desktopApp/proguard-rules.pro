# Compose Desktop release packaging runs ProGuard over dependency jars.
# kotlinx-datetime contains optional serializers that reference kotlinx.serialization;
# the desktop app does not use those serializers directly, so these optional
# references can be safely ignored during shrinking.
-dontwarn kotlinx.serialization.**
-dontwarn kotlinx.datetime.**

# Compose/AWT probes this macOS-only JDK internals class reflectively.
-dontwarn sun.font.CFont
