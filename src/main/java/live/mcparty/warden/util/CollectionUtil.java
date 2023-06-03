package live.mcparty.warden.util;

import java.util.Collection;

public class CollectionUtil {
    public static boolean containsAny(Collection<?> a, Collection<?> b) {
        for (Object o : b) {
            if (a.contains(o)) return true;
        }
        return false;
    }
}
