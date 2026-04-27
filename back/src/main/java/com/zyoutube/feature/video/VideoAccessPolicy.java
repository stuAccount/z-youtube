package com.zyoutube.feature.video;

import com.zyoutube.feature.video.model.entity.Video;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;

/**
 * DRAFT：只有作者自己能看；不进公开列表；不允许普通评论。
 * PUBLISHED + PRIVATE：只有作者自己能看；不进公开列表；不允许普通评论。
 * PUBLISHED + UNLISTED：拿到直链的人能看；不进公开列表/搜索；允许评论。
 * PUBLISHED + PUBLIC：所有人能看(包括未登录用户)；进公开列表/搜索；允许评论。
 * ARCHIVED：先定义成只有作者自己能看，外部不可见。
 */
public final class VideoAccessPolicy {
    private VideoAccessPolicy() {
    }

    public static boolean canViewDetail(Video video, Long viewerId) {
        // viewerId == null means unauthenticated user here
        if (video.isOwnedBy(viewerId)) {
            return true;
        }

        if (video.getStatus() != VideoStatus.PUBLISHED) {
            return false;
        }

        return switch (video.getVisibilityOrDefault()) {
            case PUBLIC, UNLISTED -> true;
            case PRIVATE -> false;
        };
    }

    public static boolean canAppearInPublicList(Video video) {
        return video.getStatus() == VideoStatus.PUBLISHED
                && video.getVisibilityOrDefault() == VideoVisibility.PUBLIC;
    }

    public static boolean canReadComments(Video video, Long viewerId) {
        return canViewDetail(video, viewerId);
    }

    public static boolean canCreateComment(Video video, Long viewerId) {
        return canViewDetail(video, viewerId)
                && video.getStatus() == VideoStatus.PUBLISHED
                && video.getVisibilityOrDefault() != VideoVisibility.PRIVATE;
    }
}
