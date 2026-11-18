/**
 * Copyright 2026 crackpipe
 * Duolingo 6.105.1 anchors.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.Fingerprint

object MaxUserInitFingerprint : Fingerprint(
    definingClass = "Lcfa0;",
    returnType = "V",
    parameters = listOf("Lcom/duolingo/data/user/User;"),
    name = "<init>"
)
object SubscriptionTenureInitFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/user/SubscriptionTenure;",
    returnType = "V",
    parameters = listOf("I", "Ljava/lang/Long;", "Lcom/duolingo/data/user/SubscriptionItemType;"),
    name = "<init>"
)
object HasSubscriptionFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/user/SubscriberLevel;",
    returnType = "Z",
    name = "hasSubscription"
)
object AdsConfigFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/ads/AdsConfig;",
    returnType = "Z",
    name = "b"
)
object ShopItemInitFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/shop/ShopItem;",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;", "Ljava/lang/String;", "I", "Ljava/lang/String;", "Ljava/lang/String;", "Ljava/lang/String;", "Ljava/lang/Long;"),
    name = "<init>"
)
object ShopItemPriceFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/shop/ShopItem;",
    returnType = "I",
    name = "e"
)
object ShopItemIAPItemPriceFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/shop/ShopItem\$IAPItem;",
    returnType = "I",
    name = "e"
)
object IsImmersiveFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/user/SubscriptionItemType;",
    returnType = "Z",
    name = "isImmersive"
)
object HasVideoCallFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/debug/monetization/AggressiveHookCoursePreset;",
    returnType = "Z",
    name = "getHasVideoCall"
)
object TimerBoostsInitFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/user/TimerBoosts;",
    returnType = "V",
    parameters = listOf("I", "I", "Z"),
    name = "<init>"
)
object TimerBoostsSyntheticInitFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/user/TimerBoosts;",
    returnType = "V",
    parameters = listOf("I", "I", "I", "Z"),
    name = "<init>"
)
object EnergyConfigInitFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/energy/models/EnergyConfig;",
    returnType = "V",
    parameters = listOf("I", "I", "J", "J", "J", "I", "J", "Ljava/lang/Integer;"),
    name = "<init>"
)
object EnergyConfigSyntheticFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/energy/models/EnergyConfig;",
    returnType = "V",
    parameters = listOf("I", "I", "I", "J", "J", "J", "I", "J", "Ljava/lang/Integer;", "Z"),
    name = "<init>"
)
