package io.gncloud.coin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import io.gncloud.coin.server.service.IdentityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

/*
 * create joonwoo 2018. 4. 12.
 * 
 */

public class TokenTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TokenTest.class);
    @Autowired
    private IdentityService identityService;
    @Test
    public void tokenTest() throws UnsupportedEncodingException {

        DecodedJWT decoded = JWT.decode("eyJraWQiOiJGcm9lZEZMNXBkaWlvMlh5emhsXC9xT3h2RmpUcWVPUzRNOTNpSFBhSjFEcz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI3YzIxYmJjZS1jMzljLTRkMTMtOTY4Ny1hYTdkOGMwZjE5ZmMiLCJldmVudF9pZCI6ImUwZTIzYTMxLTNkZmUtMTFlOC05MTU2LTUzNTU5ZWFkZjAyYyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1MjM1MDI1NTQsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5hcC1ub3J0aGVhc3QtMi5hbWF6b25hd3MuY29tXC9hcC1ub3J0aGVhc3QtMl84VWxWdUZGdmEiLCJleHAiOjE1MjM1MDYxNTQsImlhdCI6MTUyMzUwMjU1NCwianRpIjoiOTNiOWUwNDItOGFkZC00Yjk3LWEzZDYtYjdlNmQzMmRjZDNlIiwiY2xpZW50X2lkIjoiNGttODNqYnQxZDZwZzQxNXE0aWVxdDQxYjAiLCJ1c2VybmFtZSI6InRlc3QifQ.TzMLxLCgEfhL5CPOTJzScD5sHsrUnjYxGTR2kFY0iaSiFAg-i7UfnbwnA7S-tyAtbKJRO0sQ5GkE5eVWF3boa6Xg5z1KHLtbD_CkkxFSrySMe4QbA8bMkJ12a8r7yt9TWV6wbQuGwLX0o-ROzX4zLhmpl1ibfG_adzS4D0mfBXmKdg_FT-QZVYP5gbFyzSQI9QT4a70a6sODtT-H_GlzNuIfLAIa4M6rINelHLXnrqjvWpxMfdS1oGFWJ2ww82rn2vH3y4ug4eSuL3RcsV0N4qLGyu6qguDgif3fcan4hCCGUUYV2wCrlPir5XRnYBhA3A99H0syVFxAn74a5qQcFA");
//        identityService.parsePayload("eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.ZAkOAGvvAswmYth8xa6BQwXMCuICGN7IvGelCu1j9hV1tXp4FZKFcUJxZk-Kgdfpzqtx4GimmSq3zARvMcqZSWoyhGx6ThJI9MKyUuJAfmol04yvqetqsHEhuckYLPbhNhuKY97hHK8-RPLEdFmUVy0ntq8uTz_VqlyVagmeEj_n15QDV55WcuBQDJG0Euju2hNpBaggujwewzXaMWT1X_wYNS26NsUv4_0-_khk3SlqZjjjnLKLxT6fI-J4DPUXJe-iph5mcdHUhxJdBOJdjM-5IDmXNbrqRqP_49bmCdYz3U6Sr7mbfIASdmrJjiPaXywCUPm8MGFe1VMyXUKmFQ.pn6O1y1VzW3LVEBx.9Qh79lRmItFw5BufgTj2OYUDtFsjYUo4UbwvYprYvsPVCml5M2lRlTRVXGM8QRj7t4ZnBZVV-K65QrOfi2mFaqk95KfCXvTfPi6wEi5OFMUR0_UXvKHQ7IVbOhBQQlyzysM9yuUahx3HGW2coAxlVSAOh-MC9KN7qaS1DZeJu4doXKl2-LRX1rPtj5sWAvDbxZMjzeLe9XtzgRd0mBBAfyENsOT3n3LnKzpdLlvFR2quUIyOhbtmlPC2-uqSUoqfvuACQL6XQEfQU08uXTVHKPzUtzsqMFIup10vKQXusZtVN99g7kMT17MCIhT6MiJy8oO7yJtsg_P5Bje9PAKjJ47OUajEswGI2R6CKyZ5dfAIB3V9QtcWsTX2bBdXf2L0xvKR2CdhoGaZksyt75ipRkN9yxGVdblnY0lNX5NUNWsEezByqG27-yc-mR40N8BIqs-trITbbO5YgUGwQL0HJSQ5pujfK_VoXpH5N3zSKB48QpeL_4TtgRzDdyLIF_QW4sS9ndSdq7is5a8Er1U3O7jWR1Zj9RwurwHUdoMqtvNqbkdJrmO1qNAK8de-ltsJmnIJm6grUPlakdapHWEDdFmVGyf57p2BE5FU2OpI89UwE8F9OPp-UaQXCTa3bRTz5gPKa28Oj3_vMAztTmCVrVRHA8Xh8QcDGEktT2j1fQvINCRZQqjuX56DMaNYTRYG7Dgpd8AgDm4cUItdN3U989EvG93kayXW1x-59JgqSV5k6pJuaHFUgcV9OeR00dE2OzMhOm4r-m-Uv2zVDksbhiUGHsC1sF49i19lafxpxow0ZfXgzppEmz-fwPKoVbCheURZDMjkDjOUrs-8hP0KhVUiL6flN8t0KXejLNa-TW9murGNqb1Vze-OSTcJ94WGFT24bLtmCk_amFSM8NoTnC7gLe85cnXMOHSbQv4buHJJRhWiSvNGAocuSCmoOYiXYPOb9CEimdpdtc1kZpo49kkWCuUO-l-ExhyFaS5DUmUvW6mgCo464HwRh3einf07CTQ3HChNNnY5bnSH--vjiBA5Xazf3hBNwm1BilBSnziYhYmzE2JkMRclhf40vKSVZf1N7mjoQNNIsmGQvyZM1Fr6WvNyxAk9kQNDpJtDA7lZ9dVx-KanlDeO3ae-HUabVtPMvWzVHkbk31BxR_chFJAqGO3_PdG0zDy3zP6mo7ICVqttxbFiOagPj1N2Bb-F1ZwtT9wf3pXdVrK51yZRYUtwPsISUzPQSG8T609-0431A8YvkPNjCJ3LJRMT829fI8RwJVvw_eOfn7SPvbU.BAOMQlwWeZMQGVi0zF2oww");
        String payload = decoded.getPayload();
        byte[] decodedPayload = Base64.getUrlDecoder().decode(payload.getBytes("utf-8"));
        String payloadJson = new String(decodedPayload, "utf-8");
        Gson gson = new Gson();
        Map<String, String> payloadMap = gson.fromJson(payloadJson, Map.class);


    }

    @Test
    public void email() {
        DecodedJWT decoded = JWT.decode("eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.w7u2vssI0ajCfQChpBL8nAcvJ06NJ7XYopqQfsY0_tawmlinkMTeHbVB8ISQ0oWoSgc7f4jqAez_cpGa7-eK9W3lcq3MLS-HehsQ_9IuK69Lc0IhPB3GT2Jxxo-S8y0BzPSmmMVhDAA1EJo6FV2HNRyW_3ZRrTiMh_nOQqDrv5u65GF5m8jPep_993txG1hsDslmoiyxXKCew8fcIrhFGJY8eODGImnR0u8dL2U3jMTP3Wuk3VTgdzPK0gv9mpyeqT1ZlCL_tky69qjxfyFCc7KbJmfBKLzWyNPtKeMDzaCyhV5dCqRoSY_Xs8ylsK7WpJ6Qqqfcqg3Cwjp-wmOqng.RJtkmoSGbPrdLJSM._03Hfbf1CmqSOtjt92Mt3ZKa4HAUhkX4GnX2jdG-quqnrLC6lLlxXUe1qEMYZvyDYfcQJjcJ8rrNbs7UChiI9KVMu9FDDnzheaftmt9s9Z9Mt4Z_HzQPS_J1XAIH8uVv-IOKdxT-eO-w1kA2jwPCtXrnB-6Pd92wmAbn1hgI8k8MPTeXv8kX8JT8TSZG33WuKmNnGBVUfyL89nHo1L7Eizsdg3Uifz7CD-_mD5-ruGZpcckzn__8ypYL2_TNoGRe0VYfiCNaR8W6ZoszBkN5vyjEuRy9iV-8dIyOEIxLc2ui9llL__2beKiBBnSPKFBFMA7pWM2e9hammRfis1i2QcrZzV4zYmzobr9ayJW_qWD6q144JvM0bmkVNqrz18MB3Fpz3EMQdVMTsOfZO1rO3RbR24kFPc1WBBPM0vevItPTvdrSi89JsdmI2txxr_L-XloUEClScU-dzed7JroNamCc_8i51YLJOp2Vzk0UbaUJwnae9qzbofeAUixwBmbD-Ju9ubofjJ-xbhGr1CYUI5nlcT3kTfyujpdnaaRlRN0YMfiyq_ygWMM8Esr1MOh9AhSlEea4uD-KpsTe3wNvDZpIlWPbuKALMIc6RpfhJE_85hCc9udW0iDQbnQEWkznbZ7jOZxA_ceMrC2UBOvmqZaWHo5PplKoH-Wc-_Fu-67CHFuvQL44kM3NhcyZowt1ZzCph7iYaVUbJ4Fdjkq0Mb9byNG6BHcRPtosTxLV6jN_4qdPyP2wdI9qX-pH9PStnQj69Utm3qt0k3nWGZwzgnhhbdbMbMpAd5kWTIkjdE7CQAI74T5XbW-zj60yuduEzCwh3_Hu9z5asxEXDwpA2LZ5I9iv5XiTzNYa_qqd4ISmJC0KJKE8Ukx01IFXFSwIxFO_Dp8FvLwE4b2PcfQ2cCrgCuDUGLTjMqlmwQ_gAYwRZBcWp9rU1eCCRnaQyK3p84gAaq8skhHSccH3UiWx7SoVttmwXnTaQ0246PooYWX24eY45Bw6NC0XO5S3bo_p5PfWhsIxZfXgjSCQQlFhbXEGZxRq885IH6nFfnyHOlAtMhzH2RDVQtmF9KhshTPFR4td-NoOtpu8bleGioZgK1rQEeRn0TGxb1qj1xRV7CT8eQugkDMvN8PM5JxREn3oBQQWUY-Ly5DfAsHeWXtiz56uoqAGHgtKsPWMuOdumgJomADsGSQDUNC6IzW7-NqLBaef9D1sQrn5kifYbJsR8Gq0FesYZ6fvltkfPf7TOm1FlNEPfu1RAWKI_sXJaDlwPvVQFY3-Nx-dWUSnRqAWB__dBQ.QJ0cffhc3fqIzTMHFma0ww");
        logger.info("decoded: {}", decoded);
    }

    @Test
    public void digestTest() {
        String userId = "joonwoo";
        String e1 = DigestUtils.sha256Hex(userId);
        String e2 = DigestUtils.sha256Hex(userId);
        userId = "joonwoo1";
        String e3 = DigestUtils.sha256Hex(userId);

        System.out.println("sha256hex(joonwoo) e1: " + e1 );
        System.out.println("sha256hex(joonwoo) e2: " + e2 );
        System.out.println("sha256hex(joonwoo1) e3: " + e3 );
        System.out.println("equals: " + e1.equals(e2) );
        System.out.println("equals: " + e1.equals(e3) );

    }

}