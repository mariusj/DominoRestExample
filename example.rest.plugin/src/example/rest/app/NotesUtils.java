package example.rest.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import lotus.domino.Session;

/**
 * A set of utility classes for managing Notes specific resources.
 * 
 * This class can be replaced by using OpenNTF Domino API (ODA):
 * https://www.openntf.org/main.nsf/project.xsp?r=project/OpenNTF%20Domino%20API
 *
 * Creating the session could be done by the Factory class from the above plugin.
 */
public class NotesUtils {

	private static Method getSessionMethod(final String name, final Class<?>... parameterTypes) {
		return AccessController.doPrivileged(new PrivilegedAction<Method>() {
			@Override
			public Method run() {
				try {
					Method m = lotus.domino.local.Session.class.getDeclaredMethod(name, parameterTypes);
					m.setAccessible(true);
					;
					return m;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	// ----------------------- FindOrCreateSession
	private static Method M_FindOrCreateSession = getSessionMethod("FindOrCreateSession", long.class, int.class);

	private static lotus.domino.local.Session FindOrCreateSession(final long cpp_id, final int unknown)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return (lotus.domino.local.Session) M_FindOrCreateSession.invoke(M_FindOrCreateSession, cpp_id, unknown);
	}

	// ----------------- createNativeSession --------------
	private static Method M_NCreateSession = getSessionMethod("NCreateSession", int.class);

	private static long NCreateSession(final int unknown)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return (Long) M_NCreateSession.invoke(M_NCreateSession, unknown);
	}

	public static Session createSession() {
		try {
			long cpp = NCreateSession(0); // don't know what parameter means
			return FindOrCreateSession(cpp, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
