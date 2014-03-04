package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

public final class ServerRole {

	/**
	 * roles
	 */

	public static enum roleEnum {
		COORDINATOR, DBSERVER, UNKNOWN
	}

	/**
	 * the server role
	 */

        private roleEnum role;
    
	/**
	 * creates a server role object
	 */
	
        public ServerRole () {
                this.role = null;
        }
	
        /**
	 * creates a server role object
	 */

	public ServerRole (String name) {
		this.role = fromString(name);
	}

	/**
	 * get the server role from the object
	 */

        public roleEnum get () {
            return role;
        }

	/**
	 * convert a string to a role
	 */

        public static roleEnum fromString (String name) {
                if (name.equals("COORDINATOR")) {
                    return roleEnum.COORDINATOR;
                }
                else if (name.equals("DBSERVER")) {
                    return roleEnum.DBSERVER;
                }
                
                return roleEnum.UNKNOWN;
        }
	
}
