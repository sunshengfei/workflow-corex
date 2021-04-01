package com.fuwafuwa.utils;

import androidx.annotation.Nullable;

/**
 * Defines common MIME types and helper methods.
 */
public final class MimeTypes {

    public static final String BASE_TYPE_VIDEO = "video";
    public static final String BASE_TYPE_AUDIO = "audio";
    public static final String BASE_TYPE_TEXT = "text";
    public static final String BASE_TYPE_APPLICATION = "application";

    public static final String VIDEO_MP4 = BASE_TYPE_VIDEO + "/mp4";
    public static final String VIDEO_WEBM = BASE_TYPE_VIDEO + "/webm";
    public static final String VIDEO_H263 = BASE_TYPE_VIDEO + "/3gpp";
    public static final String VIDEO_H264 = BASE_TYPE_VIDEO + "/avc";
    public static final String VIDEO_H265 = BASE_TYPE_VIDEO + "/hevc";
    public static final String VIDEO_VP8 = BASE_TYPE_VIDEO + "/x-vnd.on2.vp8";
    public static final String VIDEO_VP9 = BASE_TYPE_VIDEO + "/x-vnd.on2.vp9";
    public static final String VIDEO_AV1 = BASE_TYPE_VIDEO + "/av01";
    public static final String VIDEO_MP4V = BASE_TYPE_VIDEO + "/mp4v-es";
    public static final String VIDEO_MPEG = BASE_TYPE_VIDEO + "/mpeg";
    public static final String VIDEO_MPEG2 = BASE_TYPE_VIDEO + "/mpeg2";
    public static final String VIDEO_VC1 = BASE_TYPE_VIDEO + "/wvc1";
    public static final String VIDEO_DIVX = BASE_TYPE_VIDEO + "/divx";
    public static final String VIDEO_DOLBY_VISION = BASE_TYPE_VIDEO + "/dolby-vision";
    public static final String VIDEO_UNKNOWN = BASE_TYPE_VIDEO + "/x-unknown";

    public static final String AUDIO_MP4 = BASE_TYPE_AUDIO + "/mp4";
    public static final String AUDIO_AAC = BASE_TYPE_AUDIO + "/mp4a-latm";
    public static final String AUDIO_WEBM = BASE_TYPE_AUDIO + "/webm";
    public static final String AUDIO_MPEG = BASE_TYPE_AUDIO + "/mpeg";
    public static final String AUDIO_MPEG_L1 = BASE_TYPE_AUDIO + "/mpeg-L1";
    public static final String AUDIO_MPEG_L2 = BASE_TYPE_AUDIO + "/mpeg-L2";
    public static final String AUDIO_RAW = BASE_TYPE_AUDIO + "/raw";
    public static final String AUDIO_ALAW = BASE_TYPE_AUDIO + "/g711-alaw";
    public static final String AUDIO_MLAW = BASE_TYPE_AUDIO + "/g711-mlaw";
    public static final String AUDIO_AC3 = BASE_TYPE_AUDIO + "/ac3";
    public static final String AUDIO_E_AC3 = BASE_TYPE_AUDIO + "/eac3";
    public static final String AUDIO_E_AC3_JOC = BASE_TYPE_AUDIO + "/eac3-joc";
    public static final String AUDIO_AC4 = BASE_TYPE_AUDIO + "/ac4";
    public static final String AUDIO_TRUEHD = BASE_TYPE_AUDIO + "/true-hd";
    public static final String AUDIO_DTS = BASE_TYPE_AUDIO + "/vnd.dts";
    public static final String AUDIO_DTS_HD = BASE_TYPE_AUDIO + "/vnd.dts.hd";
    public static final String AUDIO_DTS_EXPRESS = BASE_TYPE_AUDIO + "/vnd.dts.hd;profile=lbr";
    public static final String AUDIO_VORBIS = BASE_TYPE_AUDIO + "/vorbis";
    public static final String AUDIO_OPUS = BASE_TYPE_AUDIO + "/opus";
    public static final String AUDIO_AMR_NB = BASE_TYPE_AUDIO + "/3gpp";
    public static final String AUDIO_AMR_WB = BASE_TYPE_AUDIO + "/amr-wb";
    public static final String AUDIO_FLAC = BASE_TYPE_AUDIO + "/flac";
    public static final String AUDIO_ALAC = BASE_TYPE_AUDIO + "/alac";
    public static final String AUDIO_MSGSM = BASE_TYPE_AUDIO + "/gsm";
    public static final String AUDIO_UNKNOWN = BASE_TYPE_AUDIO + "/x-unknown";

    public static final String TEXT_VTT = BASE_TYPE_TEXT + "/vtt";
    public static final String TEXT_SSA = BASE_TYPE_TEXT + "/x-ssa";

    public static final String APPLICATION_MP4 = BASE_TYPE_APPLICATION + "/mp4";
    public static final String APPLICATION_WEBM = BASE_TYPE_APPLICATION + "/webm";
    public static final String APPLICATION_MPD = BASE_TYPE_APPLICATION + "/dash+xml";
    public static final String APPLICATION_M3U8 = BASE_TYPE_APPLICATION + "/x-mpegURL";
    public static final String APPLICATION_SS = BASE_TYPE_APPLICATION + "/vnd.ms-sstr+xml";
    public static final String APPLICATION_ID3 = BASE_TYPE_APPLICATION + "/id3";
    public static final String APPLICATION_CEA608 = BASE_TYPE_APPLICATION + "/cea-608";
    public static final String APPLICATION_CEA708 = BASE_TYPE_APPLICATION + "/cea-708";
    public static final String APPLICATION_SUBRIP = BASE_TYPE_APPLICATION + "/x-subrip";
    public static final String APPLICATION_TTML = BASE_TYPE_APPLICATION + "/ttml+xml";
    public static final String APPLICATION_TX3G = BASE_TYPE_APPLICATION + "/x-quicktime-tx3g";
    public static final String APPLICATION_MP4VTT = BASE_TYPE_APPLICATION + "/x-mp4-vtt";
    public static final String APPLICATION_MP4CEA608 = BASE_TYPE_APPLICATION + "/x-mp4-cea-608";
    public static final String APPLICATION_RAWCC = BASE_TYPE_APPLICATION + "/x-rawcc";
    public static final String APPLICATION_VOBSUB = BASE_TYPE_APPLICATION + "/vobsub";
    public static final String APPLICATION_PGS = BASE_TYPE_APPLICATION + "/pgs";
    public static final String APPLICATION_SCTE35 = BASE_TYPE_APPLICATION + "/x-scte35";
    public static final String APPLICATION_CAMERA_MOTION = BASE_TYPE_APPLICATION + "/x-camera-motion";
    public static final String APPLICATION_EMSG = BASE_TYPE_APPLICATION + "/x-emsg";
    public static final String APPLICATION_DVBSUBS = BASE_TYPE_APPLICATION + "/dvbsubs";
    public static final String APPLICATION_EXIF = BASE_TYPE_APPLICATION + "/x-exif";
    public static final String APPLICATION_ICY = BASE_TYPE_APPLICATION + "/x-icy";

    /**
     * Returns whether the given string is an audio MIME type.
     */
    public static boolean isAudio(@Nullable String mimeType) {
        return BASE_TYPE_AUDIO.equals(getTopLevelType(mimeType));
    }

    /**
     * Returns whether the given string is a video MIME type.
     */
    public static boolean isVideo(@Nullable String mimeType) {
        return BASE_TYPE_VIDEO.equals(getTopLevelType(mimeType));
    }

    /**
     * Returns whether the given string is a text MIME type.
     */
    public static boolean isText(@Nullable String mimeType) {
        return BASE_TYPE_TEXT.equals(getTopLevelType(mimeType));
    }

    /**
     * Returns whether the given string is an application MIME type.
     */
    public static boolean isApplication(@Nullable String mimeType) {
        return BASE_TYPE_APPLICATION.equals(getTopLevelType(mimeType));
    }

    /**
     * Returns true if it is known that all samples in a stream of the given sample MIME type are
     * guaranteed to be sync samples (i.e. is guaranteed to be set on
     * every sample).
     *
     * @param mimeType The sample MIME type.
     * @return True if it is known that all samples in a stream of the given sample MIME type are
     * guaranteed to be sync samples. False otherwise, including if {@code null} is passed.
     */
    public static boolean allSamplesAreSyncSamples(@Nullable String mimeType) {
        if (mimeType == null) {
            return false;
        }
        // TODO: Add additional audio MIME types. Also consider evaluating based on Format rather than
        // just MIME type, since in some cases the property is true for a subset of the profiles
        // belonging to a single MIME type. If we do this, we should move the method to a different
        // class. See [Internal ref: http://go/exo-audio-format-random-access].
        switch (mimeType) {
            case AUDIO_MPEG:
            case AUDIO_MPEG_L1:
            case AUDIO_MPEG_L2:
            case AUDIO_RAW:
            case AUDIO_ALAW:
            case AUDIO_MLAW:
            case AUDIO_OPUS:
            case AUDIO_FLAC:
            case AUDIO_AC3:
            case AUDIO_E_AC3:
            case AUDIO_E_AC3_JOC:
                return true;
            default:
                return false;
        }
    }


    /**
     * Derives a mimeType from MP4 object type identifier, as defined in RFC 6381 and
     * https://mp4ra.org/#/object_types.
     *
     * @param objectType The objectType identifier to derive.
     * @return The mimeType, or null if it could not be derived.
     */
    @Nullable
    public static String getMimeTypeFromMp4ObjectType(int objectType) {
        switch (objectType) {
            case 0x20:
                return com.google.android.exoplayer2.util.MimeTypes.VIDEO_MP4V;
            case 0x21:
                return com.google.android.exoplayer2.util.MimeTypes.VIDEO_H264;
            case 0x23:
                return com.google.android.exoplayer2.util.MimeTypes.VIDEO_H265;
            case 0x60:
            case 0x61:
            case 0x62:
            case 0x63:
            case 0x64:
            case 0x65:
                return com.google.android.exoplayer2.util.MimeTypes.VIDEO_MPEG2;
            case 0x6A:
                return com.google.android.exoplayer2.util.MimeTypes.VIDEO_MPEG;
            case 0x69:
            case 0x6B:
                return com.google.android.exoplayer2.util.MimeTypes.AUDIO_MPEG;
            case 0xA3:
                return com.google.android.exoplayer2.util.MimeTypes.VIDEO_VC1;
            case 0xB1:
                return com.google.android.exoplayer2.util.MimeTypes.VIDEO_VP9;
            case 0x40:
            case 0x66:
            case 0x67:
            case 0x68:
                return com.google.android.exoplayer2.util.MimeTypes.AUDIO_AAC;
            case 0xA5:
                return com.google.android.exoplayer2.util.MimeTypes.AUDIO_AC3;
            case 0xA6:
                return com.google.android.exoplayer2.util.MimeTypes.AUDIO_E_AC3;
            case 0xA9:
            case 0xAC:
                return com.google.android.exoplayer2.util.MimeTypes.AUDIO_DTS;
            case 0xAA:
            case 0xAB:
                return com.google.android.exoplayer2.util.MimeTypes.AUDIO_DTS_HD;
            case 0xAD:
                return com.google.android.exoplayer2.util.MimeTypes.AUDIO_OPUS;
            case 0xAE:
                return com.google.android.exoplayer2.util.MimeTypes.AUDIO_AC4;
            default:
                return null;
        }
    }

    /**
     * Returns the top-level type of {@code mimeType}, or null if {@code mimeType} is null or does not
     * contain a forward slash character ({@code '/'}).
     */
    @Nullable
    private static String getTopLevelType(@Nullable String mimeType) {
        if (mimeType == null) {
            return null;
        }
        int indexOfSlash = mimeType.indexOf('/');
        if (indexOfSlash == -1) {
            return null;
        }
        return mimeType.substring(0, indexOfSlash);
    }

    private MimeTypes() {
    }

}
