/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.duolingo v6.101.2 — do not edit by hand.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.Fingerprint

object IsPromoFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/sessionend/api/SessionEndMessageType;",
    name = "isPromo"
)

object IsGoldSubscriptionFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/shop/Inventory\$PowerUp;",
    name = "isGoldSubscription"
)

object IsSubscriptionFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/shop/Inventory\$PowerUp;",
    name = "isSubscription"
)

object HasSubscriptionFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/user/SubscriberLevel;",
    name = "hasSubscription"
)
