import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

def Save(path, list, flag):
    cred = credentials.Certificate({
        "type": "service_account",
        "project_id": "galvanic-axle-343014",
        "private_key_id": "60d6ab518257658e2af0ee5f475c5ab3693ba16c",
        "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCgrtGvFAsNVl1X\ntN8PhPYEkGsFr1y18rtQucU+FjotyXNmpzmHIrDk5nR/KzTcFR5K0uPM9cJVFTSq\ntffTCS9dbdQaG8irVsy5h44WYnOCEd6aOShgb86zzPI50QjP9b2rufBljd2Bxi92\nO7i+xfu/aL/tv7tOUyOayIGQQfZDusO8kTrA3+QuNK53iB9bzPKgxjfZ8lRhHgNi\nGRRlX69BEk0y5h4LyQnMqGVzNUy3Y2XNq6t3xaAL3Z3kSTYsFhzwoOj6Ao0HjqCQ\nr4GBYyl4ofkNlUsHg1zXvApOj5eOHwBYxIPGK3msolknypJu4O9iHPxkFhp0XxiW\n5LaIq1DPAgMBAAECggEAMuU80JZqK8bT2SCW2O0WGfr+kzXpCU7Byz+UfOaAdhlN\nntHQFIBGBLMEanVukFl2F2A2PKkJbXbCkGIEBgnuQUs0+DIdvTvIq+mGdYuop6Xu\nWu5I3kb785KylOKjxm9BrT+/qAMoCt+SEAK5sV+3rnjI9k8ZTqVh7TieZWmnTes8\n2c7y9sgRJCcKyc0j6sOsVtxEiw51XPN/JIjrIWwcOqYYBq5C2AKNEHGWYIvQGpCA\nV6ncoTrHOV/ZCxo704rMg05Q+jC7gkLLL5x+//DwvBXRI8oJ29g/N9g5tOcmkA9/\nsyRG1mHMksqmIiuQpckwkuGRGvyQXjJJc5uHAUKAvQKBgQDfQohfFCT/wj2o8MB1\n00S3bDCXJVYBHltf41yeEmc2lOl0vhI2NQcGUSob/6j91rNxXuFmTK69k072i2qC\nhItse19neMZO4P4pGqA895anFzcLCuvJHITaP/4Z2DWzIWSPTjlHMVYqqh7qDCNZ\nW5ONX7a1Zn8i5G6P0ry/+i4CmwKBgQC4PxBjyJBMJvN9Np7xyIRlB6zTdIBNE8am\n0wuQ0NsQKliGtVWfYiBfji5YUoz44taICTgC/fkKgxNn3gSxOk6P/4WD13qJc6TE\nKb7BRJWnL5teQ4pHwQz8ytdt9dmxOxJP6KLzsHsGQRpufMrShywhawf6gtzeSLTR\nxlVAiNXD3QKBgGmFKa0+eSpEpR33BA/sVbsXsHOPmvGWZnuQm1K+wfejNCAQQc1L\nDNHohK2NDVNEKbW4sXHHZoOFXznZtPKRMNCKExJ1m8zmPFozEm8eWh8JMuPOOpjo\nGMaKnk+ax+6tJrkwsJO8dsxdcZUPPZnbVYbpKRLdqdNVAgiKAtn9pcdnAoGAPKSt\nPVqvfBE6BZWr3UM1qJdNIFBxRm1i7lf6r5C++eQmPTiEVTCUHT+MK5AITIdFO4Nl\nRz7W8MnR4lcmTjs1zpm5FXsgHwvMSLDA05ZCd8PorK8oXZPCNZaCL/RC0d3ymhCl\nSfZll9pn28QBcigBs4IqHx9hmVxu/7j7KIGhcpUCgYEA2+BCZmUp/LtNFbSMdiRi\n25gvItbWiHNhqfFuK7inaFDUQbidL9j6lE/5GTNHtCHdm2yk3LstwfE8QOoKLJOc\nthVSQym3ezeeBWBDeixM82XcI7hamgTjnLrODSRU9sCzBq+aD2A2t05NdUDvPwNt\nMfQOCOobIdqA0EwKRcNkJzM=\n-----END PRIVATE KEY-----\n",
        "client_email": "galvanic-axle-343014@appspot.gserviceaccount.com",
        "client_id": "103988747013869983347",
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
        "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/galvanic-axle-343014%40appspot.gserviceaccount.com"
    })
    if not flag:
        def_app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref = db.reference(path)
    FBlist = ref.get()
    keys = [i for i in FBlist.keys() if i.isdigit()]
    for i in keys:
        del FBlist[i]
    FBlist.update(dict(zip([i for i in range(len(list))], list)))
    ref.set(FBlist)




