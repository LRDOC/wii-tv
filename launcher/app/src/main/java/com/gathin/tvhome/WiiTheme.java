package com.gathin.tvhome;

import android.content.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A colour theme for the channel menu, chosen in the Colors channel and kept in SharedPreferences, so it survives
 * the TV turning off and the menu's process being killed.
 *
 * WiiBase still computes the console's shading in grey and recolours it through map(): each grey is placed on a
 * line between two of the theme's colours, so the stripes, the panel's grooves and the button gloss carry over to
 * every theme. CLASSIC skips the mapping and keeps the original pixels exactly.
 */
final class WiiTheme {
    private static final String PREFS = "wii_theme";
    private static final String KEY = "theme";

    final String id;
    final String name;
    final boolean dark;
    final int bg, stripe, panel, panel2, accent, text, tileFill, tileBorder, btn;
    /** Text on an accent-coloured fill: near-black or white, whichever reads better. */
    final int onAccent;

    // What the menu draws in each part. CLASSIC keeps the console's own greys; the rest come from the palette.
    final int clock, date, frame, label, wordmark, envelope, envelopeFlap, plainTop, plainBottom, emptyFill, notice,
            noticeText, noticeRule;

    private WiiTheme(String id, String name, boolean dark, int bg, int stripe, int panel, int panel2, int accent,
            int text, int tileFill, int tileBorder, int btn) {
        this.id = id;
        this.name = name;
        this.dark = dark;
        this.bg = bg;
        this.stripe = stripe;
        this.panel = panel;
        this.panel2 = panel2;
        this.accent = accent;
        this.text = text;
        this.tileFill = tileFill;
        this.tileBorder = tileBorder;
        this.btn = btn;
        onAccent = contrast(accent, 0xFF141414) >= contrast(accent, 0xFFFFFFFF) ? 0xFF141414 : 0xFFFFFFFF;
        boolean classic = "classic".equals(id);
        clock = classic ? 0xFF9A9A9A : text;
        date = classic ? 0xFF7C7C7C : text;
        frame = classic ? 0xFFB5B5B5 : tileBorder;
        label = classic ? 0xFF7A7A7A : text;
        wordmark = classic ? 0xFFA4A4A4 : text;
        envelope = classic ? 0xFF939393 : text;
        envelopeFlap = classic ? 0xFFE2E2E2 : btn;
        plainTop = classic ? 0xFFFFFFFF : tileFill;
        plainBottom = classic ? 0xFFE9E9E9 : blend(tileFill, tileBorder, 0.35f);
        emptyFill = classic ? 0xFFE9E9E9 : bg;
        notice = classic ? 0xFFFFFFFF : bg;
        noticeText = classic ? 0xFF1E1E1E : text;
        noticeRule = classic ? 0xFFDADADA : tileBorder;
    }

    boolean isClassic() {
        return this == CLASSIC;
    }

    private static WiiTheme t(String id, String name, boolean dark, int... c) {
        return new WiiTheme(id, name, dark, c[0] | 0xFF000000, c[1] | 0xFF000000, c[2] | 0xFF000000,
                c[3] | 0xFF000000, c[4] | 0xFF000000, c[5] | 0xFF000000, c[6] | 0xFF000000, c[7] | 0xFF000000,
                c[8] | 0xFF000000);
    }

    // bg, stripe, panel, panel2, accent, text, tile fill, tile border, button face
    static final WiiTheme CLASSIC = t("classic", "Classic", false,
            0xEDEDED, 0xE3E3E3, 0xCDCFD3, 0xB4B6BA, 0x3FC0EC, 0x4F5257, 0xF7F7F7, 0xB5B5B5, 0xE6E7E9);

    static final List<WiiTheme> ALL = Collections.unmodifiableList(Arrays.asList(
            CLASSIC,
            t("blush", "Blush", false, 0xFBEDEF, 0xF5E1E5, 0xEFCFD6, 0xE3B9C3, 0xE86A8E, 0x6E2F41, 0xFFF7F8, 0xEBC6CF, 0xF9E3E8),
            t("rose", "Rose", false, 0xFCEBEA, 0xF6DEDC, 0xF0CBC8, 0xE5B3AF, 0xD9534F, 0x722724, 0xFFF6F5, 0xEDC4C1, 0xFAE1DF),
            t("peach", "Peach", false, 0xFDF0E6, 0xF8E4D5, 0xF3D3BB, 0xEABFA0, 0xF08A4B, 0x6E3A1B, 0xFFF8F2, 0xEFCDB4, 0xFBE7D8),
            t("butter", "Butter", false, 0xFCF7E3, 0xF6EED2, 0xEFE2B6, 0xE3D199, 0xD9A916, 0x5E4A0E, 0xFFFCF0, 0xE9DCAE, 0xF9F1D6),
            t("lime", "Lime", false, 0xF3F8E4, 0xE8F0D3, 0xDAE6B8, 0xC8D99C, 0x86B32A, 0x3F5717, 0xFBFDF3, 0xD6E3B3, 0xEEF5DB),
            t("mint", "Mint", false, 0xE9F7EF, 0xDCF0E4, 0xC6E6D3, 0xADD9BF, 0x36B873, 0x1E5638, 0xF6FCF8, 0xC3E3D0, 0xE0F3E8),
            t("sage", "Sage", false, 0xEEF2EB, 0xE3E9DE, 0xD2DBCB, 0xBDC9B4, 0x6F9160, 0x384A2F, 0xF8FAF6, 0xCFD9C7, 0xE6ECE2),
            t("seafoam", "Seafoam", false, 0xE6F6F5, 0xD8EFED, 0xC0E3E0, 0xA6D6D2, 0x1FA79E, 0x1A524E, 0xF4FBFB, 0xBCE0DD, 0xDCF1EF),
            t("sky", "Sky", false, 0xE8F3FC, 0xDAEBF8, 0xC3DDF2, 0xA9CCEA, 0x2C8DDB, 0x1C466B, 0xF5FAFE, 0xBFD9EF, 0xDEEDF9),
            t("periwinkle", "Periwinkle", false, 0xECEEFB, 0xE0E3F6, 0xCCD1EF, 0xB5BCE6, 0x5A6CD4, 0x2C366E, 0xF7F8FE, 0xC9CEEE, 0xE3E6F8),
            t("lavender", "Lavender", false, 0xF1ECFA, 0xE7E0F5, 0xD9CEEE, 0xC7B8E3, 0x8A66D6, 0x46336D, 0xFAF7FF, 0xD5C9EC, 0xEAE3F8),
            t("lilac", "Lilac", false, 0xF7EDF8, 0xEFE0F1, 0xE5CDE8, 0xD6B6DB, 0xB15CBE, 0x5A2B61, 0xFDF7FE, 0xE3C8E7, 0xF1E2F3),
            t("sand", "Sand", false, 0xF6F1E8, 0xEEE7DA, 0xE2D7C3, 0xD3C5AA, 0xB08D50, 0x55432A, 0xFCFAF6, 0xDED2BC, 0xF0E9DC),
            t("cloud", "Cloud", false, 0xEEF1F4, 0xE3E7EC, 0xD0D6DE, 0xB9C1CC, 0x62798F, 0x34404E, 0xF8F9FB, 0xCCD3DB, 0xE6EAEF),
            t("midnight", "Midnight", true, 0x121A2B, 0x172036, 0x222C45, 0x192237, 0x34BEED, 0xDCE6F5, 0x1C2540, 0x34405E, 0x2A3553),
            t("charcoal", "Charcoal", true, 0x1B1C1F, 0x212226, 0x2C2E33, 0x222327, 0xA7B0BB, 0xE8E9EC, 0x26272B, 0x3E4046, 0x34363C),
            t("plum", "Plum", true, 0x1D1525, 0x241B2E, 0x30243D, 0x251B30, 0xC28DF2, 0xEFE3FB, 0x2A2034, 0x473858, 0x3A2C4A),
            t("forest", "Forest", true, 0x111B16, 0x16221C, 0x1E2E26, 0x16241D, 0x58D398, 0xDDF2E6, 0x1A2821, 0x33493D, 0x26392F),
            t("oled", "OLED Black", true, 0x000000, 0x080808, 0x111111, 0x060606, 0x34BEED, 0xF2F2F2, 0x0C0C0C, 0x2A2A2A, 0x171717)));

    static WiiTheme byId(String id) {
        for (WiiTheme theme : ALL) if (theme.id.equals(id)) return theme;
        return CLASSIC;
    }

    static WiiTheme load(Context context) {
        return byId(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, CLASSIC.id));
    }

    static void save(Context context, WiiTheme theme) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, theme.id).apply();
    }

    /**
     * The colour for grey v, on the line through (v0, c0) and (v1, c1), carried on past either end so the console's
     * highlights and grooves keep their depth.
     */
    static int map(float v, float v0, int c0, float v1, int c1) {
        float f = (v - v0) / (v1 - v0);
        return 0xFF000000 | (channel(c0 >> 16, c1 >> 16, f) << 16) | (channel(c0 >> 8, c1 >> 8, f) << 8)
                | channel(c0, c1, f);
    }

    private static int channel(int a, int b, float f) {
        a &= 255;
        b &= 255;
        int v = Math.round(a + (b - a) * f);
        return v < 0 ? 0 : Math.min(255, v);
    }

    static int blend(int a, int b, float f) {
        return 0xFF000000 | (channel(a >> 16, b >> 16, f) << 16) | (channel(a >> 8, b >> 8, f) << 8) | channel(a, b, f);
    }

    private static double luminance(int c) {
        double[] v = {((c >> 16) & 255) / 255.0, ((c >> 8) & 255) / 255.0, (c & 255) / 255.0};
        for (int i = 0; i < 3; i++) v[i] = v[i] <= 0.03928 ? v[i] / 12.92 : Math.pow((v[i] + 0.055) / 1.055, 2.4);
        return 0.2126 * v[0] + 0.7152 * v[1] + 0.0722 * v[2];
    }

    /** WCAG contrast ratio between two colours. */
    static double contrast(int a, int b) {
        double la = luminance(a), lb = luminance(b);
        return (Math.max(la, lb) + 0.05) / (Math.min(la, lb) + 0.05);
    }
}
