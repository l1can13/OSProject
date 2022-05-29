import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from itertools import chain

def load_shared_list(userId):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref = db.reference("User_Shared/" + userId)

    user_shared_dict = ref.get()

    user_shared_list = []
    if not user_shared_dict is None:
        for i in list(user_shared_dict.values()):
            user_shared_list.append(list(i.values()))

        user_shared_list = list(chain.from_iterable(user_shared_list))
        for i in range(len(user_shared_list)):
            if isinstance(user_shared_list[i], int):
                user_shared_list[i] = str(user_shared_list[i])

    return user_shared_list

def GetUserInfo_by_id_or_email_or_phone(pattern, cur_id):
    #pattern - email or id
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
    ref = db.reference("User_Info")
    user_info = ["email", "id", "phoneNumber", "username"]
    Fblist = ref.get()
    array = []
    for i in list(Fblist):
        array = [Fblist[i][info] for info in user_info]
        if pattern in array:
            break
        else:
            array.clear()
    return set_new_shared_list(array, user_info, cur_id)

def set_new_shared_list(array, user_info, cur_id):
    ref = db.reference("User_Shared/" + cur_id)

    user_shared_dict = ref.get()
    if user_shared_dict == None:
        user_shared_dict = {}
        user_shared_dict[array[1]] = dict(zip(user_info, array))
    else:
        user_shared_dict[array[1]] = dict(zip(user_info, array))

    ref.set(user_shared_dict)
    user_shared_list = []
    for i in list(user_shared_dict.values()):
        user_shared_list.append(list(i.values()))

    user_shared_list = list(chain.from_iterable(user_shared_list))
    for i in range(len(user_shared_list)):
        if isinstance(user_shared_list[i], int):
            user_shared_list[i] = str(user_shared_list[i])

    ref = db.reference("User_Shared_ID/" + array[1])
    Shared_IDs_List = ref.get()

    if Shared_IDs_List is None:
        ref.set({cur_id: "0"})
    else:
        Shared_IDs_List[cur_id] = "0"
        ref.set(Shared_IDs_List)

    return user_shared_list

def PhoneAlreadyRegistred(phone):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})

    ref = db.reference("User_Info")

    Fblist = ref.get()
    if not Fblist is None:
        for i in list(Fblist):
            if phone == Fblist[i]["phoneNumber"]:
                return False

    return True

#поделился уже
def isAlreadyShared(pattern, userId):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref = db.reference("User_Shared/" + userId)
    Fblist = ref.get()

    user_info = ["email", "id", "phoneNumber"]
    if not Fblist is None:
        for i in list(Fblist):
            array = [Fblist[i][info] for info in user_info]
            if pattern in array:
                return True
            else:
                array.clear()

    return False


#это ли id
def IsCorrectId(probably_id):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref = db.reference("User_Info")

    Fblist = ref.get()

    for i in list(Fblist):
        if probably_id == Fblist[i]["id"]:
            return True

    return False

def delete_by_name(name, id):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref = db.reference("User_Shared/" + id)

    FBList = ref.get()

    for i in list(FBList):
        if FBList[i.strip()]["username"] == name:
            delete_id = FBList[i.strip()]["id"]
            del FBList[i.strip()]
            ref_id = db.reference("User_Shared_ID/" + i.strip())
            FBlist_id = ref_id.get()
            del FBlist_id[id]
            ref_files = db.reference("User_Data/" + delete_id + "/Shared/" + id + "-folder")
            ref_files.set([])
            ref_id.set(FBlist_id)

    ref.set(FBList)

def get_shared_status(cur_id):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})

    ref = db.reference("User_Shared_ID/" + cur_id)
    FBList = ref.get()
    if not FBList is None:
        for i in list(FBList):
            if FBList[i] == "0":
                return True

    return False

def setAllAccept(cur_id):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})

    ref = db.reference("User_Shared_ID/" + cur_id)
    FBList = ref.get()
    for i in list(FBList):
        if FBList[i] == "0":
            FBList[i] = "1"

    ref.set(FBList)

def load_shared_folder_list(cur_id):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref_id = db.reference("User_Shared_ID/" + cur_id)

    Fblist = ref_id.get()
    if isinstance(Fblist, dict):
        return [i.strip() + "-folder" for i in Fblist.keys()]
    else:
        return []

def isYou(pattern, cur_id):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref = db.reference("User_Info")

    FBList = ref.get()

    user_info = ["email", "id", "phoneNumber"]

    for i in list(FBList):
        array = [FBList[i.strip()][info].strip() for info in user_info]
        if pattern in array and array[1] == cur_id:
            return True
    return False

def get_shared_users_for_home(cur_id):
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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})
    ref = db.reference("User_Shared/" + cur_id)
    FBList = ref.get()
    email_list = []
    for i in list(FBList):
        email_list.append(FBList[i]["email"])

    return email_list


def get_id_by_email(email_list):
    ref = db.reference("User_Info")
    FBList = ref.get()

    return [FBList[i]["id"] for i in list(FBList) if FBList[i]["email"] in email_list]

def delete_shared_files(cur_id, path):
    """Удаляем файлы у пользователей, с которыми делились"""

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
    #получаем список id тех, у кого удаляем файлы
    id_list = get_id_by_email(get_shared_users_for_home(cur_id))
    if len(id_list) > 0:
        #получаем нужный нам путь
        x = path.split("/")
        x = [i for i in x if i]
        path = ""
        for i in range(len(x) - 1):
            path += x[i]
        file_name = x[len(x)-1]
        for id in id_list:
            ref = db.reference("User_Data/"+id+"/Shared/"+cur_id+"-folder/"+path)
            FBList = ref.get()
            if isinstance(FBList, list):
                FBList.remove(file_name)
                ref.set(FBList)
            else:
                ckecker = 0
                if file_name.endswith("-folder"):
                    for i in list(FBList):
                        if i == file_name:
                            del FBList[i]
                            ckecker = 2
                else:
                    for i in list(FBList):
                        if FBList[i] == file_name:
                            del FBList[i]
                            ckecker = 1
                if ckecker == 1:
                    #перебираем ключи, чтоб всё шло по порядку
                    int_keys = [i for i in FBList.keys() if i.isdigit()]
                    int_keys_values = [FBList[i] for i in int_keys]
                    for i in list(FBList):
                        if i.isdigit():
                            del FBList[i]
                    new_keys = [i for i in range(len(int_keys_values))]
                    FBList.update(dict(zip(new_keys, int_keys_values)))
                    ref.set(FBList)
                elif ckecker == 2:
                    ref.set(FBList)

def share_files(users_list, files_list, path_to_files):
    """ Делимся выбранными файлами с выбранными пользователями """

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
        def_app = firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://galvanic-axle-343014-default-rtdb.firebaseio.com'})

    #print(users_list)
    #print(files_list)
    #print(path_to_files)

    ref = db.reference(path_to_files)
    FBList = ref.get()
    #проверяем, есть ли в списке файлов папки
    dict_for_set = {}
    for i in list(files_list):
        if i in FBList:
            dict_for_set[i] = FBList[i]

    #формируем список индексов для не папок
    keys = [str(i) for i in range(len(files_list) - len(dict_for_set))]
    #обновляем список файлов, убирая папки
    files_list = [i for i in files_list if not i in FBList]

    #обновляем наш словарь
    dict_for_set.update(dict(zip(keys, files_list)))

    #обновляем путь до наших файлов
    # "User_Data/id/Shared/cur_id-folder/path"
    x = path_to_files.split("/")
    x = [i for i in x if i]
    cur_id = x[1]
    path = ""
    for i in range(3, len(x)):
        path += x[i]
    #добавляем файлы в наш список
    id_list = get_id_by_email(list(users_list))
    for id in id_list:
        ref_shared = db.reference("User_Data/"+id+"/Shared/"+cur_id+"-folder/"+path)
        #проверка на то, есть ли такие файлы уже в списке
        Shared_list = ref_shared.get()
        if isinstance(Shared_list, dict):
            for i in list(Shared_list):
                for j in list(dict_for_set):
                    if dict_for_set[j] == Shared_list[i]:
                        del dict_for_set[j]

            #добавляем наш список к итоговому
            keys = [i for i in Shared_list.keys() if i.isdigit()]
            length = len(keys)
            keys = [i for i in range(int(keys[length-1])+1, length + len(dict_for_set))]

            dict_for_set = [dict_for_set[i] for i in dict_for_set]

            Shared_list.update(dict(zip(keys, dict_for_set)))
            ref_shared.set(Shared_list)
        elif isinstance(Shared_list, list):
            print(Shared_list)
            for i in list(dict_for_set):
                if i.isdigit():
                    for j in range(len(Shared_list)):
                        if dict_for_set[i] == Shared_list[j]:
                            Shared_list.remove(dict_for_set[i])
                            break

            keys = [i for i in dict_for_set.keys() if i.isdigit()]
            length = len(keys)
            if length > 0:
                keys = [i for i in range(int(keys[length - 1]) + 1, length + len(Shared_list))]
            dict_for_set.update(dict(zip(keys, Shared_list)))
            ref_shared.set(dict_for_set)
        else:
            ref_shared.set(dict_for_set)