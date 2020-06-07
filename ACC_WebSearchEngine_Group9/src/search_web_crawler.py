import requests
from bs4 import BeautifulSoup
import os
import re
from time import sleep
clear = lambda: os.system('cls')
clear()
WEBLEN = 1500
INITIAL = 'http://www.uwindsor.ca'
webLists = []
webLists.append(INITIAL)
file1 = open("websites.txt","w") 
file1.write(INITIAL+'\n') 
def web(webUrl):
    try:
        url = webUrl
        code = requests.get(url)
        plain = code.text
        s = BeautifulSoup(plain, "html.parser")
        for link in s.findAll('a'):
            tet_2 = link.get('href')
            if tet_2 != None:
                find = re.findall("http",tet_2)
                if find:
                    if len(webLists)<WEBLEN:
                        if tet_2 not in webLists:
                            file1.write(tet_2+'\n') 
                            webLists.append(tet_2)
    except requests.exceptions.ConnectionError:
        sleep(5)
print("START!!!!!!!!!!\n\n")
for i in webLists:
    if len(webLists)<WEBLEN:
        web(i)
    else:
        break
print(webLists)
print(len(webLists))
print("\n\nENDD!!!!!!!!!!")

file1.close()