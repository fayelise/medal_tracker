package com.medaltracker.olympic.aspect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Aspect pour le logging d'audit des requêtes HTTP.
 * Intercepte toutes les requêtes avant leur traitement par les contrôleurs.
 */
@Aspect
@Component
public class AuditLogAspect {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT_LOGGER");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Intercepte toutes les méthodes des contrôleurs Spring.
     * Log les informations d'audit avant le traitement de la requête.
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        
        if (request != null) {
            String dateTime = LocalDateTime.now().format(DATE_FORMATTER);
            String user = getUser(request);
            String ipAddress = getClientIpAddress(request);
            String endpoint = getEndpoint(request);
            String parameters = getParametersWithMaskedPassword(request);
            
            String auditLog = String.format(
                "[%s] | User: %s | IP: %s | Endpoint: %s | Parameters: %s",
                dateTime, user, ipAddress, endpoint, parameters
            );
            
            AUDIT_LOGGER.info(auditLog);
        }
        
        return joinPoint.proceed();
    }

    /**
     * Récupère la requête HTTP courante.
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * Récupère l'utilisateur depuis la requête.
     * Peut être étendu pour utiliser Spring Security.
     */
    private String getUser(HttpServletRequest request) {
        // Si Spring Security est configuré, utiliser: SecurityContextHolder.getContext().getAuthentication()
        String user = request.getHeader("X-User");
        if (user == null || user.isEmpty()) {
            user = request.getRemoteUser();
        }
        if (user == null || user.isEmpty()) {
            user = "anonymous";
        }
        return user;
    }

    /**
     * Récupère l'adresse IP du client.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    /**
     * Récupère l'endpoint (URL) sollicité.
     */
    private String getEndpoint(HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI();
    }

    /**
     * Récupère les paramètres de la requête avec masquage du mot de passe.
     */
    private String getParametersWithMaskedPassword(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        
        // Paramètres de requête (query parameters)
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            
            if (params.length() > 0) {
                params.append(", ");
            }
            
            if (isPasswordField(paramName)) {
                params.append(paramName).append("=***");
            } else {
                params.append(paramName).append("=").append(paramValue);
            }
        }
        
        // Body JSON pour les requêtes POST/PUT
        if (request instanceof CachedBodyHttpServletRequest) {
            String body = ((CachedBodyHttpServletRequest) request).getCachedBody();
            if (body != null && !body.isEmpty()) {
                if (params.length() > 0) {
                    params.append(", ");
                }
                params.append("Body: ").append(maskPasswordsInJson(body));
            }
        }
        
        return params.toString();
    }

    /**
     * Masque les passwords dans une chaîne JSON.
     */
    private String maskPasswordsInJson(String json) {
        String masked = json;
        String[] passwordFields = {"password", "pwd", "pass", "secret", "motDePasse", "motdepasse"};
        
        for (String field : passwordFields) {
            // Masque les passwords dans les formats: "field":"value" ou "field": "value"
            masked = masked.replaceAll(
                "(?i)(\"" + field + "\"\\s*:\\s*\")([^\"]*)(\")",
                "$1***$3"
            );
        }
        
        return masked;
    }

    /**
     * Vérifie si un champ est un champ de mot de passe.
     */
    private boolean isPasswordField(String fieldName) {
        String lowerFieldName = fieldName.toLowerCase();
        return lowerFieldName.contains("password") 
            || lowerFieldName.contains("pwd") 
            || lowerFieldName.contains("pass")
            || lowerFieldName.contains("secret");
    }
}
