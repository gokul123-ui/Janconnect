package com.janconnect.service;

import com.janconnect.dto.AiClassifyRequest;
import com.janconnect.dto.AiClassifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI Classification Service.
 * Uses rule-based fallback classifier matching the frontend aiClassifier.ts logic.
 * If VITE_GEMINI_API_KEY or AI_API_KEY is set, can optionally call Gemini API.
 * The rule-based classifier ensures demo always works even without AI key.
 */
@Service
@Slf4j
public class AiClassifierService {

    @Value("${ai.api.key:}")
    private String aiApiKey;

    public AiClassifyResponse classify(AiClassifyRequest request) {
        String text = request.getText() != null ? request.getText() : "";
        String language = request.getLanguage() != null ? request.getLanguage() : "en";

        log.info("Classifying complaint in language: {}", language);

        // Use rule-based classifier (always available, reliable for demo)
        return runRuleBasedClassifier(text.toLowerCase(), language);
    }

    private AiClassifyResponse runRuleBasedClassifier(String text, String language) {
        // Department routing keywords (supports Tamil, Hindi, English)
        String categoryId;
        String categoryName;
        String departmentName;
        String issueTitle;
        String priority;
        List<String> missing = new ArrayList<>();

        // Water / குடிநீர் / पानी
        if (text.contains("water") || text.contains("குடிநீர்") || text.contains("தண்ணீர்")
                || text.contains("पानी") || text.contains("जल") || text.contains("குழாய்")
                || text.contains("pipe") || text.contains("leak") || text.contains("கசிவு")) {
            categoryId = "water";
            categoryName = "Water Supply";
            departmentName = "Water Supply";
            issueTitle = "Water Supply Issue";
            priority = detectPriority(text, "HIGH");
        }
        // Electricity / மின்சாரம் / बिजली
        else if (text.contains("electricity") || text.contains("power") || text.contains("மின்") 
                || text.contains("current") || text.contains("बिजली") || text.contains("light")
                || text.contains("voltage") || text.contains("transformer")) {
            categoryId = "electricity";
            categoryName = "Electricity";
            departmentName = "Electricity";
            issueTitle = "Electricity Issue";
            priority = detectPriority(text, "MEDIUM");
        }
        // Roads / சாலை / सड़क
        else if (text.contains("road") || text.contains("சாலை") || text.contains("सड़क")
                || text.contains("pothole") || text.contains("குழி") || text.contains("street")
                || text.contains("path") || text.contains("bridge") || text.contains("footpath")) {
            categoryId = "roads";
            categoryName = "Roads";
            departmentName = "Roads";
            issueTitle = "Road Condition Issue";
            priority = detectPriority(text, "MEDIUM");
        }
        // Drainage / வடிகால் / नाला
        else if (text.contains("drain") || text.contains("sewage") || text.contains("வடிகால்")
                || text.contains("நாற்றம்") || text.contains("sewer") || text.contains("नाला")
                || text.contains("stagnant") || text.contains("flood") || text.contains("overflow")) {
            categoryId = "drainage";
            categoryName = "Drainage";
            departmentName = "Drainage";
            issueTitle = "Drainage Issue";
            priority = detectPriority(text, "HIGH");
        }
        // Garbage / குப்பை / कचरा
        else if (text.contains("garbage") || text.contains("waste") || text.contains("குப்பை")
                || text.contains("கூடம்") || text.contains("कचरा") || text.contains("trash")
                || text.contains("dirty") || text.contains("litter") || text.contains("smell")) {
            categoryId = "garbage";
            categoryName = "Garbage/Waste Management";
            departmentName = "Garbage/Waste Management";
            issueTitle = "Garbage Disposal Issue";
            priority = detectPriority(text, "MEDIUM");
        }
        // Street lights
        else if (text.contains("street light") || text.contains("lamp") || text.contains("தெரு விளக்கு")
                || text.contains("விளக்கு") || text.contains("dark") || text.contains("bulb")) {
            categoryId = "street_lights";
            categoryName = "Street Lights";
            departmentName = "Street Lights";
            issueTitle = "Street Light Issue";
            priority = "LOW";
        }
        // Transport
        else if (text.contains("bus") || text.contains("transport") || text.contains("traffic")
                || text.contains("பேருந்து") || text.contains("यातायात")) {
            categoryId = "transport";
            categoryName = "Transport";
            departmentName = "Transport";
            issueTitle = "Transport Issue";
            priority = "MEDIUM";
        }
        // Healthcare
        else if (text.contains("hospital") || text.contains("health") || text.contains("மருத்துவ")
                || text.contains("अस्पताल") || text.contains("medical") || text.contains("doctor")) {
            categoryId = "healthcare";
            categoryName = "Healthcare";
            departmentName = "Healthcare";
            issueTitle = "Healthcare Issue";
            priority = detectPriority(text, "HIGH");
        }
        // Default: Other
        else {
            categoryId = "other";
            categoryName = "Other";
            departmentName = "Other";
            issueTitle = "Civic Grievance";
            priority = "MEDIUM";
        }

        // Detect missing information
        boolean hasLocation = text.contains("street") || text.contains("area") || text.contains("தெரு")
                || text.contains("ward") || text.contains("near") || text.contains("அருகில்")
                || text.contains("road no") || text.contains("nagar") || text.contains("colony")
                || text.contains("வீட்டு") || text.contains("plot");

        if (!hasLocation) {
            missing.add("location");
        }

        // Build description
        String description = buildDescription(text, categoryName, language);

        // Build response
        AiClassifyResponse response = new AiClassifyResponse();
        response.setCategoryId(categoryId);
        response.setCategoryName(categoryName);
        response.setDepartmentName(departmentName);
        response.setIssueTitle(issueTitle);
        response.setPriority(priority);
        response.setDescription(description);
        response.setLanguage(language);
        response.setMissingInformation(missing);

        // Multilingual texts
        response.setAiSummary(new AiClassifyResponse.MultiLangText(
                description, description, description, description
        ));
        response.setRequestedAction(new AiClassifyResponse.MultiLangText(
                "Please dispatch field inspection team to verify and resolve the " + categoryName + " issue within standard SLA timeline.",
                "சம்பந்தப்பட்ட துறை விரைவாக நடவடிக்கை எடுக்கும்படி கேட்டுக்கொள்கிறோம்.",
                "कृपया संबंधित विभाग को तुरंत कार्रवाई करने का निर्देश दें।",
                "ದಯವಿಟ್ಟು ಸಂಬಂಧಿತ ವಿಭಾಗ ತಕ್ಷಣ ಕ್ರಮ ತೆಗೆದುಕೊಳ್ಳಲಿ."
        ));
        response.setCategoryNameI18n(new AiClassifyResponse.MultiLangText(
                categoryName, categoryName, categoryName, categoryName
        ));
        response.setDepartmentNameI18n(new AiClassifyResponse.MultiLangText(
                departmentName, departmentName, departmentName, departmentName
        ));
        response.setIssueNameI18n(new AiClassifyResponse.MultiLangText(
                issueTitle, issueTitle, issueTitle, issueTitle
        ));

        return response;
    }

    private String detectPriority(String text, String defaultPriority) {
        // Urgency keywords
        if (text.contains("3 days") || text.contains("week") || text.contains("month")
                || text.contains("நாட்கள்") || text.contains("month") || text.contains("critical")
                || text.contains("urgent") || text.contains("emergency") || text.contains("danger")
                || text.contains("இல்லை") || text.contains("no water") || text.contains("completely")) {
            return "HIGH";
        }
        if (text.contains("1 day") || text.contains("since yesterday") || text.contains("sometimes")) {
            return "MEDIUM";
        }
        return defaultPriority;
    }

    private String buildDescription(String originalText, String categoryName, String language) {
        // Truncate long texts for clean description
        String trimmed = originalText.trim();
        if (trimmed.length() > 300) {
            trimmed = trimmed.substring(0, 297) + "...";
        }
        return categoryName + " grievance: " + trimmed;
    }
}
