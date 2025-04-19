var client = {
    get: function(url) {
        return fetch(url)
            .then(response => handleResponse(response))
            .then((text) => text.length ? JSON.parse(text) : {});
    },
    delete: function(url, body) {
        return fetch(url, { method: 'DELETE', body: JSON.stringify(body) })
            .then(response => handleResponse(response))
            .then((text) => text.length ? JSON.parse(text) : {});
    },
    post: function(url, body, options = {}) {
        const headers = body instanceof FormData ? options : { 'Content-Type': 'application/json', ...options };
        return fetch(url, {
            method: 'POST',
            body: body instanceof FormData ? body : JSON.stringify(body),
            headers
        })
            .then(response => handleResponse(response))
            .then((text) => text.length ? JSON.parse(text) : {});
    },
    put: function(url, body, options) {
        return fetch(url, { method: 'PUT', body: JSON.stringify(body), headers: options })
            .then(response => handleResponse(response))
            .then((text) => text.length ? JSON.parse(text) : {});
    },
    patch: function(url, body, options) {
        return fetch(url, { method: 'PATCH', body: JSON.stringify(body), headers: options })
            .then(response => handleResponse(response))
            .then((text) => text.length ? JSON.parse(text) : {});
    }
}

function handleResponse(response) {
    if (!response.ok) { throw response }
    return response.text();
}

export default client;