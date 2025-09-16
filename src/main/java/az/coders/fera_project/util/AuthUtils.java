package az.coders.fera_project.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

    public class AuthUtils {

        public static Long extractUserId() {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof Map<?, ?> principalMap) {
                Object idObj = principalMap.get("id");
                if (idObj != null) {
                    try {
                        return Long.valueOf(idObj.toString());
                    } catch (NumberFormatException ignored) {}
                }
            }

            return null;
        }
    }


