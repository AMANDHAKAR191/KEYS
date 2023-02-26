# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Keep the google-services.json file unchanged
-keep class com.google.android.gms.common.api.GoogleApiClient{*;}
-keep class com.google.android.gms.common.api.Api{*;}
-keep class com.google.android.gms.common.api.Status{*;}
-keep class com.google.firebase.FirebaseApp{*;}
-keep class com.google.firebase.auth.FirebaseUser{*;}
-keep class com.google.firebase.auth.FirebaseAuth{*;}
-keep class com.google.firebase.database.FirebaseDatabase{*;}
-keep class com.google.firebase.firestore.FirebaseFirestore{*;}
-keep class com.google.firebase.storage.FirebaseStorage{*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{*;}
-keep class com.google.android.gms.tasks.Tasks{*;}
-keep class com.google.android.gms.tasks.Task{*;}
-keep class com.google.android.gms.tasks.Continuation{*;}
-keep class com.google.android.gms.tasks.OnCompleteListener{*;}
-keep class com.google.android.gms.tasks.OnSuccessListener{*;}
-keep class com.google.android.gms.tasks.OnFailureListener{*;}
-keep class com.google.android.gms.tasks.SuccessContinuation{*;}
-keep class com.google.android.gms.tasks.TaskCompletionSource{*;}
-keep class com.google.android.gms.tasks.RuntimeExecutionException{*;}
-keep class com.google.android.gms.tasks.zzu{*;}
-keep class com.google.firebase.messaging.RemoteMessage{*;}
-keep class com.google.firebase.messaging.RemoteMessage$Builder{*;}
-keep class com.google.firebase.messaging.RemoteMessage$Notification{*;}