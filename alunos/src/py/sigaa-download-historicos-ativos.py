
import genericpath
from optparse import OptionParser
from getpass import getpass
from selenium import webdriver
from selenium.webdriver.firefox.service import Service
from selenium.webdriver.firefox.options import Options


from bs4 import BeautifulSoup as BS
from selenium.webdriver.common.keys import Keys
from selenium.webdriver import ActionChains
from selenium.webdriver.support.ui import Select
#import org.openqa.selenium.support.ui.Select;

import time
import os
import os.path
import sys
import pickle


def login(driver, url, username, passwd):
    # login sigaa
    driver.get(url + "/sigaa/verTelaLogin.do")
    #assert "Python" in driver.title
    elem = driver.find_element("name", "user.login")
    elem.send_keys(username)
    elem = driver.find_element("name", "user.senha")
    elem.send_keys(passwd)
    elem.send_keys(Keys.RETURN)
    time.sleep(3)
    if (driver.page_source.count("rio e/ou senha inv") != 0):
        return False
    
    
    return True


def vinculo(driver, tipo_vinculo):
    actions = ActionChains(driver)
    continue_link = driver.find_element('link text', tipo_vinculo)
    actions.move_to_element(continue_link)
    actions.click(continue_link)
    actions.perform()
    time.sleep(3)  


def download_historicos(driver, dir):

    selectedPages = 'form:j_id_jsp_1517377330_415'
    actions = ActionChains(driver)
    menu_docente = driver.find_element('id', 'menu:form-menu_docente')
    actions.move_to_element(menu_docente)
    chefia = driver.find_element('id', 'chefia')
    actions.move_to_element(chefia)
    chefia_Discentes = driver.find_element('id', 'chefia_Discentes')
    actions.move_to_element(chefia_Discentes)
    chefDis_ConsulHistorico = driver.find_element('id', 'menu:chefDis_ConsulHistorico')
    actions.click(chefDis_ConsulHistorico)
    actions.perform()
    time.sleep(3)
    curso = driver.find_element('name','formulario:curso')
    curso.send_keys('CIÊNCIA DA COMPUTAÇÃO')
    curso.send_keys(Keys.RETURN)
    time.sleep(3)


    proxima_pagina = driver.find_element('name',selectedPages)
    select_object = Select(proxima_pagina)
    max_paginas = len(select_object.options) # funciona, mas está desativado
    pagina = 1
    continuar = True
    ativos = 0
    actions.reset_actions()
    baixados = 0
    print('\t\t*************** Pegando históricos')
    while continuar:
        filename = '{}/log-page-{}.txt'.format(dir, pagina)
        print('Salvando: ', filename);
        file_ptr = open(filename, 'w+')
        for i in range(0,100):
            j = 2 * (i + 1)
            mask = '/html/body/div[2]/div[2]/form[2]/table/tbody/tr[{}]/td[6]'.format(j)
            
            s = driver.find_element('xpath', mask)
            
            
            v = s.text
            if v == 'ATIVO':
                matricula_mask = '/html/body/div[2]/div[2]/form[2]/table/tbody/tr[{}] /td[2]'.format(j)
                matricula = driver.find_element('xpath', matricula_mask)
                nome_mask = '/html/body/div[2]/div[2]/form[2]/table/tbody/tr[{}]/td[4]'.format(j)
                nome = driver.find_element('xpath', nome_mask)
                line = '{};{}\n'.format(matricula.text, nome.text)
                file_ptr.write(line)
                ativos = ativos + 1
                baixados = baixados + 1
                if i == 0:
                    historico_mask = 'form:selecionarDiscente'
                else:
                    historico_mask = 'form:selecionarDiscentej_id_{}'.format(i)

                botao = driver.find_element('id',historico_mask)
                print('Botão: ', historico_mask)
                botao.send_keys(Keys.RETURN)
                file_name = 'historicos/historico_{}.pdf'.format(matricula.text)
                time.sleep(3)
                timeout = 0
                while not os.path.exists(file_name) and timeout < 10:
                    time.sleep(timeout + 1)
                    timeout = timeout + 1

                if os.path.exists(file_name):
                    print('Download file: ', file_name)
                else:
                    print('Erro no download - timeout')
        
        #/html/body/div[2]/div[2]/form[2]/div/select
        #form:j_id_jsp_1517377330_412
        #proxima_pagina = driver.find_elements_by_xpath('/html/body/div[2]/div[2]/form[2]/div/select')
        pagina = pagina + 1
        file_ptr.close()
        print('\tPagina:', pagina, 'Total baixados:', baixados, 'Ativos: ', ativos)
        baixados = 0
        if pagina > max_paginas:
            continuar = False
        else:
            mask = 'Pag. {}'.format(pagina)
            proxima_pagina = driver.find_element('name',selectedPages)
            select_object = Select(proxima_pagina)
            select_object.select_by_visible_text(mask)
            time.sleep(3)
            print('Trocando de página...', pagina)

    print('Ativos: ', ativos)
    #table1 = driver.find_elements_by_xpath('/html/body/div[2]/div[2]/form[2]/table/tbody/tr[2]')

    '''
    /html/body/div[2]/div[2]/form[2]/table/tbody/tr[2]/td[6]
    /html/body/div[2]/div[2]/form[2]/table/tbody/tr[4]/td[6]
    /html/body/div[2]/div[2]/form[2]/table/tbody/tr[200]/td[6]
    
    id=form:selectionarDiscente
    id=form:selectionarDiscentej_id_1
    id=form:selectionarDiscentej_id_99
    
    '''

if __name__ == "__main__":

    url         = 'https://sigaa.ufrrj.br'
    username    = sys.argv[1]
    passwd      = sys.argv[2]
    # Diretório onde os históricos serão salvos. É preciso passar essa variável em dois lugares:
    #   1- para criar o diretório
    #   2- para dentro do firefox
    
    #dir         = '/home/mzamith/Documents/Works/UFRRJ/UFRRJ.Disciplinas/Downloads.historicos'
    dir = sys.argv[3]
    dirpath = os.path.join(os.getcwd(), dir)
    if not os.path.exists(dirpath):
        print("Creating data dir.")
        os.mkdir(dirpath)
 
    '''
    Peferencias do navegador. Atenção para o diretório de download. Tem que ser o mesmo para onde vai ser verificado se o arquivo foi 
    ou não gravado
    '''

    profile = webdriver.FirefoxProfile()
    profile.set_preference("browser.download.folderList", 2)
    profile.set_preference("browser.download.manager.showWhenStarting", False)
    profile.set_preference("browser.download.dir", dir)
    #profile.set_preference("browser.helperApps.neverAsk.saveToDisk", "application/x-gzip")

    profile.set_preference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
    profile.set_preference("pdfjs.disabled", True)
    profile.set_preference("plugin.scan.Acrobat", "99.0")
    profile.set_preference("plugin.scan.plid.all", False)

    driver = webdriver.Firefox(firefox_profile=profile)


    while not login(driver, url, username, passwd):
        print('Tentando conexão...')

    vinculo(driver, 'Chefia/Diretoria')
    download_historicos(driver, dir)
    #driver.quit()
