/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.duolingo v6.101.2 — do not edit by hand.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.Fingerprint

object UcFingerprint : Fingerprint(
    definingClass = "Lcom/bytedance/sdk/openadsdk/eu/uc/uc/qtm;",
    returnType = "Z",
    name = "uc"
)

object IsPromoFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/sessionend/api/SessionEndMessageType;",
    returnType = "Z",
    name = "isPromo"
)

object IsGoldSubscriptionFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/shop/Inventory\$PowerUp;",
    returnType = "Z",
    name = "isGoldSubscription"
)

object IsSubscriptionFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/shop/Inventory\$PowerUp;",
    returnType = "Z",
    name = "isSubscription"
)

object HasSubscriptionFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/user/SubscriberLevel;",
    returnType = "Z",
    name = "hasSubscription"
)

object IsUnlockFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/feature/newpath/ui/items/OvalAnimationScenario;",
    returnType = "Z",
    name = "isUnlock"
)

object GetStoreIdFromSystemFingerprint : Fingerprint(
    definingClass = "Lcom/adjust/sdk/StoreInfoUtil;",
    returnType = "Ljava/lang/String;",
    name = "getStoreIdFromSystem"
)
