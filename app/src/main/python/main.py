import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

def Save(path, save_list):
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
    if not firebase_admin._apps:
        def_app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})

    ref = db.reference(path)
    FBlist = ref.get()
    x = path.split("/")
    x = [i for i in x if i]

    if save_list == [] and len(x) == 3:
        ref.set([])

    elif isinstance(FBlist, dict):
        keys = [i for i in FBlist.keys() if i.isdigit()]

        keys_string = [i for i in FBlist.keys() if not i.isdigit()]

        for i in keys:
            del FBlist[i]

        save_list = [i for i in save_list if not i in keys_string]

        FBlist.update(dict(zip([i for i in range(len(save_list))], save_list)))

        ref.set(FBlist)

    elif isinstance(FBlist, list):
        ref.set([i for i in save_list])
    elif FBlist is None and save_list is None:
        ref.set([])
    elif FBlist is None and not save_list is None:
        ref.set([i for i in save_list])
    else:
        ref.set([i for i in save_list])
    #print("SAVE", save_list)
    if x[len(x)-1].endswith("-folder"):
        folder = x[len(x)-1]
        path = ""
        for i in range(len(x)-1):
            path+=x[i] + "/"

        ref = db.reference(path)
        FBlist = ref.get()
        #print(FBlist)
        if isinstance(FBlist, dict):
            keys = [i for i in FBlist.keys() if i.isdigit()]

            for i in keys:
                if FBlist[i] == folder:
                    del FBlist[i]

        elif isinstance(FBlist, list):
            FBList_set = set()
            for i in FBlist:
                FBList_set.add(i)
            FBlist = list(FBList_set)

        if FBlist is None:
            FBlist = []
        ref.set(FBlist)

def loader(path):
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
    if not firebase_admin._apps:
        def_app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})

    FBList = db.reference(path).get()
    if isinstance(FBList, list):
        FBList_set = set()
        for i in FBList:
            FBList_set.add(i)
        return list(FBList_set)
    elif isinstance(FBList, dict):
        keys = [i for i in FBList.keys() if i.isdigit()]
        load_list = []
        for i in keys:
            load_list.append(FBList[i])
        load_list.extend([i for i in FBList.keys() if not i.isdigit()])
        return load_list
    else:
        return []

def delete(path):
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
    if not firebase_admin._apps:
        def_app = firebase_admin.initialize_app(cred, { 'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    FBList = db.reference(path).get()

    if FBList is None:
        x = path.split("/")
        x = [i for i in x if i]
        path = ""
        for i in range(len(x)-1):
            path+=x[i] + "/"
        ref = db.reference(path)
        FBList = ref.get()
        if isinstance(FBList, dict):
            keys = [i for i in FBList.keys() if i.isdigit()]
            FBList[len(keys)] = x[len(x)-1]
            ref.set(FBList)
        elif isinstance(FBList,list):
            FBList.append(x[len(x)-1])
            ref.set(FBList)
        else:
            ref.set([x[len(x)-1]])
        print(FBList)

def delete_folder(path, name):
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
    if not firebase_admin._apps:
        def_app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})

    ref = db.reference(path)
    FBList = ref.get()
    if isinstance(FBList, dict):
        for i in list(FBList):
            if i == name:
                del FBList[i]
    elif isinstance(FBList, list):
        FBList.remove(name)
    if FBList == [] or FBList == {}:
        x = path.split("/")
        x = [i for i in x if i]
        if len(x) > 2:
            ref = db.reference(back(path))
            FBList = ref.get()
            name = get_last_folder_name(path)
            for i in list(FBList):
                if i == name:
                    del FBList[i]
            keys = [i for i in FBList.keys() if i.isdigit()]
            FBList[len(keys)] = name
    if FBList is None:
        FBList = []
    ref.set(FBList)


def get_last_folder_name(path):
    x = [i for i in path.split("/") if i]
    return x[len(x)-1]

def folder_rename(path, old_name, new_name):
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
    if not firebase_admin._apps:
        def_app = firebase_admin.initialize_app(cred, { 'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref = db.reference(path)
    Fblist = ref.get()
    if isinstance(Fblist, list):
        for i in range(len(Fblist)):
            if Fblist[i] == old_name:
                Fblist[i] = new_name
    else:
        for i in list(Fblist):
            if i == old_name:
                Fblist[new_name] = Fblist[old_name]
                del Fblist[old_name]
    ref.set(Fblist)

def back(path):
    # cred = credentials.Certificate({
    #     "type": "service_account",
    #     "project_id": "galvanic-axle-343014",
    #     "private_key_id": "60d6ab518257658e2af0ee5f475c5ab3693ba16c",
    #     "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCgrtGvFAsNVl1X\ntN8PhPYEkGsFr1y18rtQucU+FjotyXNmpzmHIrDk5nR/KzTcFR5K0uPM9cJVFTSq\ntffTCS9dbdQaG8irVsy5h44WYnOCEd6aOShgb86zzPI50QjP9b2rufBljd2Bxi92\nO7i+xfu/aL/tv7tOUyOayIGQQfZDusO8kTrA3+QuNK53iB9bzPKgxjfZ8lRhHgNi\nGRRlX69BEk0y5h4LyQnMqGVzNUy3Y2XNq6t3xaAL3Z3kSTYsFhzwoOj6Ao0HjqCQ\nr4GBYyl4ofkNlUsHg1zXvApOj5eOHwBYxIPGK3msolknypJu4O9iHPxkFhp0XxiW\n5LaIq1DPAgMBAAECggEAMuU80JZqK8bT2SCW2O0WGfr+kzXpCU7Byz+UfOaAdhlN\nntHQFIBGBLMEanVukFl2F2A2PKkJbXbCkGIEBgnuQUs0+DIdvTvIq+mGdYuop6Xu\nWu5I3kb785KylOKjxm9BrT+/qAMoCt+SEAK5sV+3rnjI9k8ZTqVh7TieZWmnTes8\n2c7y9sgRJCcKyc0j6sOsVtxEiw51XPN/JIjrIWwcOqYYBq5C2AKNEHGWYIvQGpCA\nV6ncoTrHOV/ZCxo704rMg05Q+jC7gkLLL5x+//DwvBXRI8oJ29g/N9g5tOcmkA9/\nsyRG1mHMksqmIiuQpckwkuGRGvyQXjJJc5uHAUKAvQKBgQDfQohfFCT/wj2o8MB1\n00S3bDCXJVYBHltf41yeEmc2lOl0vhI2NQcGUSob/6j91rNxXuFmTK69k072i2qC\nhItse19neMZO4P4pGqA895anFzcLCuvJHITaP/4Z2DWzIWSPTjlHMVYqqh7qDCNZ\nW5ONX7a1Zn8i5G6P0ry/+i4CmwKBgQC4PxBjyJBMJvN9Np7xyIRlB6zTdIBNE8am\n0wuQ0NsQKliGtVWfYiBfji5YUoz44taICTgC/fkKgxNn3gSxOk6P/4WD13qJc6TE\nKb7BRJWnL5teQ4pHwQz8ytdt9dmxOxJP6KLzsHsGQRpufMrShywhawf6gtzeSLTR\nxlVAiNXD3QKBgGmFKa0+eSpEpR33BA/sVbsXsHOPmvGWZnuQm1K+wfejNCAQQc1L\nDNHohK2NDVNEKbW4sXHHZoOFXznZtPKRMNCKExJ1m8zmPFozEm8eWh8JMuPOOpjo\nGMaKnk+ax+6tJrkwsJO8dsxdcZUPPZnbVYbpKRLdqdNVAgiKAtn9pcdnAoGAPKSt\nPVqvfBE6BZWr3UM1qJdNIFBxRm1i7lf6r5C++eQmPTiEVTCUHT+MK5AITIdFO4Nl\nRz7W8MnR4lcmTjs1zpm5FXsgHwvMSLDA05ZCd8PorK8oXZPCNZaCL/RC0d3ymhCl\nSfZll9pn28QBcigBs4IqHx9hmVxu/7j7KIGhcpUCgYEA2+BCZmUp/LtNFbSMdiRi\n25gvItbWiHNhqfFuK7inaFDUQbidL9j6lE/5GTNHtCHdm2yk3LstwfE8QOoKLJOc\nthVSQym3ezeeBWBDeixM82XcI7hamgTjnLrODSRU9sCzBq+aD2A2t05NdUDvPwNt\nMfQOCOobIdqA0EwKRcNkJzM=\n-----END PRIVATE KEY-----\n",
    #     "client_email": "galvanic-axle-343014@appspot.gserviceaccount.com",
    #     "client_id": "103988747013869983347",
    #     "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    #     "token_uri": "https://oauth2.googleapis.com/token",
    #     "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    #     "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/galvanic-axle-343014%40appspot.gserviceaccount.com"
    # })
    # if not firebase_admin._apps:
    #     def_app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    x = path.split("/")
    x = [i for i in x if i]
    path = ""
    for i in range(len(x) - 1):
        path += x[i] + "/"
    return path
