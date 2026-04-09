# Hermes Shizuku Oracle (helper app)

Goal: expose local HTTP endpoint on 127.0.0.1:17910:
- GET /owner?ports=17890,17891
Returns JSON with port->(pid, packageName, processName)
