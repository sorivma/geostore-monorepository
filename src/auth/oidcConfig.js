const realm = process.env.REACT_APP_KEYCLOAK_REALM;
const baseUrl = process.env.REACT_APP_KEYCLOAK_URL;
const clientId = process.env.REACT_APP_KEYCLOAK_CLIENT_ID;

const oidcConfig = {
    authority: `http://localhost:18080/realms/geo`,
    client_id: clientId,
    redirect_uri: window.location.origin,
    scope: 'openid profile email',
    response_type: 'code',
};

export default oidcConfig;