    #iter: add headers
    while (True):
        path = '//div[@id="textArea"]/h3[string-length(text())>0 and text()!=" " and position()=' + str(i) + ']/text()'
        this = doctree.xpath(path)
        print this
        if this == [] and i != 1:
            break
        if this != []:
            headers.append(this)
        i += 1
    
  #  instructions = doctree.xpath('//div[@id="textArea"]/h3[string-length(text())>0]/following-sibling::*[1][string-length(text())>0 and text()!=" "]//text()')
    
