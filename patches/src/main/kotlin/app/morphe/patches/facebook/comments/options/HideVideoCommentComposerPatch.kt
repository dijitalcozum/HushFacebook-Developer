/*
 * Copyright 2026 Hushfacebook contributors
 * https://github.com/SysAdminDoc/Hushfacebook
 */
package app.morphe.patches.facebook.comments.options

import app.morphe.patches.facebook.comments.summaries.descriptor

import app.morphe.patcher.StringComparisonType
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.facebook.feed.aidetected.EXTENSION_CLASSES
import app.morphe.patches.facebook.misc.extension.enableStatus
import app.morphe.patches.facebook.misc.extension.facebookExtensionPatch
import app.morphe.patches.facebook.misc.extension.requireStatusMethod
import app.morphe.patches.facebook.misc.settings.settingsPatch
import app.morphe.patches.shared.compat.AppCompatibilities

/**
 * Hides only the comment composer shown under Facebook's full-screen video viewer.
 * This is a standalone patch so it can be selected independently in Morphe Manager.
 */
@Suppress("unused")
val hideVideoCommentComposerPatch = bytecodePatch(
    name = "Hide video comment composer",
    description = "Hide the Add a comment box under full-screen videos. Ordinary post comment boxes are unchanged. Starts off.",
) {
    category("Comments")
    dependsOn(settingsPatch, facebookExtensionPatch)
    compatibleWith(*AppCompatibilities.facebook())

    execute {
        requireStatusMethod("hideVideoCommentComposer")
        val viewer = viewerButtons(holders(VIEWER_GIF))
        val videoBar = videoCommentBar(holders(VIDEO_COMMENT_BAR), viewer.render)
        mutableClassDefBy(videoBar.definingClass).methods.single { it.descriptor() == videoBar.descriptor() }
            .skipVideoCommentComposerCreation(viewer.render)
        enableStatus("hideVideoCommentComposer")
    }
}

private fun app.morphe.patcher.patch.BytecodePatchContext.holders(string: String) =
    classDefByStrings(string, StringComparisonType.EQUALS).filterNot { it.type.startsWith(EXTENSION_CLASSES) }
