package com.focela.platform.system.config.captcha;

import com.anji.captcha.model.common.RepCodeEnum;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.impl.AbstractCaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import com.anji.captcha.util.AESUtil;
import com.anji.captcha.util.ImageUtils;
import com.anji.captcha.util.RandomUtils;
import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.Strings;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * Picture word captcha
 *
 * @since 2025/7/23 20:44
 */
public class DefaultPictureWordCaptchaService extends AbstractCaptchaService {

    /**
     * Base characters for the captcha
     */
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    /**
     * Captcha length
     */
    private static final Integer LENGTH = 4;

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int LINES = 10;

    @Override
    public void init(Properties config) {
        super.init(config);
    }

    @Override
    public void destroy(Properties config) {
        logger.info("start-clear-history-data-{}", captchaType());
    }

    @Override
    public String captchaType() {
        return "pictureWord";
    }

    @Override
    public ResponseModel get(CaptchaVO captchaVO) {
        String text = generateRandomText(LENGTH);
        CaptchaVO imageData = getImageData(text);
        // pointJson is not sent to the frontend; backend validation only, enable during testing
//        imageData.setPointJson(text);
        return ResponseModel.successData(imageData);
    }

    @Override
    public ResponseModel check(CaptchaVO captchaVO) {
        ResponseModel r = super.check(captchaVO);
        if (!validatedReq(r)) {
            return r;
        }

        // Retrieve the captcha
        String codeKey = String.format(REDIS_CAPTCHA_KEY, captchaVO.getToken());
        if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
            return ResponseModel.errorMsg(RepCodeEnum.API_CAPTCHA_INVALID);
        }
        // Correct captcha
        String codeValue = CaptchaServiceFactory.getCache(cacheType).get(codeKey);
        String code = getCodeByCodeValue(codeValue);
        String secretKey = getSecretKeyByCodeValue(codeValue);
        // Captcha is single-use; invalidate immediately
        CaptchaServiceFactory.getCache(cacheType).delete(codeKey);

        // User-entered captcha (CaptchaVO has no reserved field; temporarily use pointJson without encryption/decryption)
        String userCode = captchaVO.getPointJson();
        if (!Strings.CI.equals(code, userCode)) {
            afterValidateFail(captchaVO);
            return ResponseModel.errorMsg(RepCodeEnum.API_CAPTCHA_COORDINATE_ERROR);
        }

        // Validation succeeded; store the info in cache
        String value;
        try {
            value = AESUtil.aesEncrypt(captchaVO.getToken().concat("---").concat(userCode), secretKey);
        } catch (Exception e) {
            logger.error("AES encryption failed", e);
            afterValidateFail(captchaVO);
            return ResponseModel.errorMsg(e.getMessage());
        }
        String secondKey = String.format(REDIS_SECOND_CAPTCHA_KEY, value);
        CaptchaServiceFactory.getCache(cacheType).set(secondKey, captchaVO.getToken(), EXPIRESIN_THREE);
        captchaVO.setResult(true);
        captchaVO.resetClientFlag();
        return ResponseModel.successData(captchaVO);
    }

    @Override
    public ResponseModel verification(CaptchaVO captchaVO) {
        ResponseModel r = super.verification(captchaVO);
        if (!validatedReq(r)) {
            return r;
        }
        try {
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captchaVO.getCaptchaVerification());
            if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
                return ResponseModel.errorMsg(RepCodeEnum.API_CAPTCHA_INVALID);
            }
            // Invalidate immediately after retrieving the value for the second validation
            CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
        } catch (Exception e) {
            logger.error("Captcha parse failed", e);
            return ResponseModel.errorMsg(e.getMessage());
        }
        return ResponseModel.success();
    }


    private CaptchaVO getImageData(String text) {
        CaptchaVO dataVO = new CaptchaVO();
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Set background color
        g.setColor(getRandomColor(200, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // Draw noise lines
        for (int i = 0; i < LINES; i++) {
            g.setColor(getRandomColor(100, 200));
            int x1 = RandomUtil.randomInt(WIDTH);
            int y1 = RandomUtil.randomInt(HEIGHT);
            int x2 = RandomUtil.randomInt(WIDTH);
            int y2 = RandomUtil.randomInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }
        // Set font
        g.setFont(new Font("Arial", Font.BOLD, 24));
        // Draw captcha text
        for (int i = 0; i < text.length(); i++) {
            g.setColor(getRandomColor(20, 130));
            // Text rotation
            AffineTransform affineTransform = new AffineTransform();
            int x = 20 + i * 20;
            int y = 24 + RandomUtil.randomInt(8);
            // Rotation range -45 ~ 45
            affineTransform.setToRotation(Math.toRadians(RandomUtil.randomInt(-45, 45)), x, y);
            g.setTransform(affineTransform);
            g.drawString(text.charAt(i) + "", x, y);
        }
        // Add noise dots
        for (int i = 0; i < 100; i++) {
            int x = RandomUtil.randomInt(WIDTH);
            int y = RandomUtil.randomInt(HEIGHT);
            image.setRGB(x, y, getRandomColor(0, 255).getRGB());
        }
        g.dispose();

        String secretKey = null;
        if (captchaAesStatus) {
            secretKey = AESUtil.getKey();
        }
        dataVO.setSecretKey(secretKey);

        dataVO.setOriginalImageBase64(ImageUtils.getImageToBase64Str(image).replaceAll("\r|\n", ""));
        dataVO.setToken(RandomUtils.getUUID());
//        dataVO.setSecretKey(secretKey);
        // Store coordinate info into redis
        String codeKey = String.format(REDIS_CAPTCHA_KEY, dataVO.getToken());
        CaptchaServiceFactory.getCache(cacheType).set(codeKey, getCodeValue(text, secretKey), EXPIRESIN_SECONDS);
        return dataVO;
    }

    private String getCodeValue(String text, String secretKey) {
        return text + "," + secretKey;
    }

    private String getCodeByCodeValue(String codeValue) {
        return codeValue.split(",")[0];
    }

    private String getSecretKeyByCodeValue(String codeValue) {
        return codeValue.split(",")[1];
    }

    private Color getRandomColor(int min, int max) {
        int minVal = Math.min(min, max);
        int maxVal = Math.max(min, max);
        int r = RandomUtil.randomInt(minVal, maxVal);
        int g = RandomUtil.randomInt(minVal, maxVal);
        int b = RandomUtil.randomInt(minVal, maxVal);
        return new Color(r, g, b);
    }

    /**
     * Generate a random string of the specified length
     *
     * @param length length
     * @return {@link String}
     */
    public static String generateRandomText(int length) {
        return RandomUtil.randomString(CHARACTERS, length);
    }

}
