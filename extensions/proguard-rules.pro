-dontobfuscate
-dontoptimize
-keepattributes *
-keep class app.morphe.** {
  *;
}
-keep class standardrepo.morphe.** {
  *;
}
-keep class com.google.** {
  *;
}