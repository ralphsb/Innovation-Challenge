// Generated by CoffeeScript 1.6.2
var auth, deleteDocument, expectJSONObject, getDocument, http, makeDispatchHandler, matchDocName, nameregexes, postDocument, pump, putDocument, send200, send400, send403, send404, sendError, sendJSON, url;

http = require('http');

url = require('url');

nameregexes = {};

send403 = function(res, message) {
  if (message == null) {
    message = 'Forbidden\n';
  }
  res.writeHead(403, {
    'Content-Type': 'text/plain'
  });
  return res.end(message);
};

send404 = function(res, message) {
  if (message == null) {
    message = '404: Your document could not be found.\n';
  }
  res.writeHead(404, {
    'Content-Type': 'text/plain'
  });
  return res.end(message);
};

sendError = function(res, message, head) {
  if (head == null) {
    head = false;
  }
  if (message === 'forbidden') {
    if (head) {
      return send403(res, "");
    } else {
      return send403(res);
    }
  } else if (message === 'Document does not exist') {
    if (head) {
      return send404(res, "");
    } else {
      return send404(res);
    }
  } else {
    console.warn("REST server does not know how to send error: '" + message + "'");
    if (head) {
      res.writeHead(500, {
        'Content-Type': 'text/plain'
      });
      return res.end("Error: " + message + "\n");
    } else {
      res.writeHead(500, {});
      return res.end("");
    }
  }
};

send400 = function(res, message) {
  res.writeHead(400, {
    'Content-Type': 'text/plain'
  });
  return res.end(message);
};

send200 = function(res, message) {
  if (message == null) {
    message = "OK\n";
  }
  res.writeHead(200, {
    'Content-Type': 'text/plain'
  });
  return res.end(message);
};

sendJSON = function(res, obj) {
  res.writeHead(200, {
    'Content-Type': 'application/json'
  });
  return res.end(JSON.stringify(obj) + '\n');
};

expectJSONObject = function(req, res, callback) {
  return pump(req, function(data) {
    var error, obj;

    try {
      obj = JSON.parse(data);
    } catch (_error) {
      error = _error;
      send400(res, 'Supplied JSON invalid');
      return;
    }
    return callback(obj);
  });
};

pump = function(req, callback) {
  var data;

  data = '';
  req.on('data', function(chunk) {
    return data += chunk;
  });
  return req.on('end', function() {
    return callback(data);
  });
};

matchDocName = function(urlString, base) {
  var parts, urlParts;

  if (nameregexes[base] == null) {
    if (base == null) {
      base = "";
    }
    if (base[base.length - 1] === "/") {
      base = base.slice(0, -1);
    }
    nameregexes[base] = new RegExp("^" + base + "\/doc\/(?:([^\/]+?))\/?$", "i");
  }
  urlParts = url.parse(urlString);
  parts = urlParts.pathname.match(nameregexes[base]);
  if (parts) {
    return parts[1];
  }
};

auth = function(req, res, createClient, cb) {
  var data;

  data = {
    headers: req.headers,
    remoteAddress: req.connection.remoteAddress
  };
  return createClient(data, function(error, client) {
    if (client) {
      return typeof cb === "function" ? cb(req, res, client) : void 0;
    } else {
      return sendError(res, error);
    }
  });
};

getDocument = function(req, res, client) {
  return client.getSnapshot(req.params.name, function(error, doc) {
    if (doc) {
      res.setHeader('X-OT-Type', doc.type.name);
      res.setHeader('X-OT-Version', doc.v);
      if (req.method === "HEAD") {
        return send200(res, "");
      } else {
        if (typeof doc.snapshot === 'string') {
          return send200(res, doc.snapshot);
        } else {
          return sendJSON(res, doc.snapshot);
        }
      }
    } else {
      if (req.method === "HEAD") {
        return sendError(res, error, true);
      } else {
        return sendError(res, error);
      }
    }
  });
};

putDocument = function(req, res, client) {
  return expectJSONObject(req, res, function(obj) {
    var meta, type;

    type = obj != null ? obj.type : void 0;
    meta = obj != null ? obj.meta : void 0;
    if (!(typeof type === 'string' && (meta === void 0 || typeof meta === 'object'))) {
      return send400(res, 'Type invalid');
    } else {
      return client.create(req.params.name, type, meta, function(error) {
        if (error) {
          return sendError(res, error);
        } else {
          return send200(res);
        }
      });
    }
  });
};

postDocument = function(req, res, client) {
  var query, version;

  query = url.parse(req.url, true).query;
  version = (query != null ? query.v : void 0) != null ? parseInt(query != null ? query.v : void 0) : parseInt(req.headers['x-ot-version']);
  if (!((version != null) && version >= 0)) {
    return send400(res, 'Version required - attach query parameter ?v=X on your URL or set the X-OT-Version header');
  } else {
    return expectJSONObject(req, res, function(obj) {
      var opData;

      opData = {
        v: version,
        op: obj,
        meta: {
          source: req.socket.remoteAddress
        }
      };
      return client.submitOp(req.params.name, opData, function(error, newVersion) {
        if (error != null) {
          return sendError(res, error);
        } else {
          return sendJSON(res, {
            v: newVersion
          });
        }
      });
    });
  }
};

deleteDocument = function(req, res, client) {
  return client["delete"](req.params.name, function(error) {
    if (error) {
      return sendError(res, error);
    } else {
      return send200(res);
    }
  });
};

makeDispatchHandler = function(createClient, options) {
  return function(req, res, next) {
    var name;

    if (name = matchDocName(req.url, options.base)) {
      req.params || (req.params = {});
      req.params.name = name;
      switch (req.method) {
        case 'GET':
        case 'HEAD':
          return auth(req, res, createClient, getDocument);
        case 'PUT':
          return auth(req, res, createClient, putDocument);
        case 'POST':
          return auth(req, res, createClient, postDocument);
        case 'DELETE':
          return auth(req, res, createClient, deleteDocument);
        default:
          return next();
      }
    } else {
      return next();
    }
  };
};

module.exports = makeDispatchHandler;
