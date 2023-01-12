#--coding:utf-8--

from urlparse import urlparse, urljoin
from os.path import splitext, dirname, isdir, exists
from os import sep, unlink, makedirs
from string import replace, find, lower
from urllib import urlretrieve
from htmllib import HTMLParser
from formatter import AbstractFormatter, DumbWriter
from cStringIO import StringIO


class Retriever(object): # download web pages
    def __init__(self, url):
        self.url = url
        self.file = self.filename(url)
        
    def filename(self, url, deffile='index.html'):
        parsedurl = urlparse(url, 'http:', 0)  # parse path
        path = parsedurl[1] + parsedurl[2]
        print path
        ext = splitext(path)
        print ext
        if ext[1] == '': # no file, use default
            if path[-1] == '/':
                path += deffile
            else:
                path += '/' + deffile
        ldir = dirname(path) # local directory
        print path
        print ldir
        if sep != '/': # os-indep. path separator
            ldir = replace(ldir, '/', sep)
        if not isdir(ldir): # create archive dir if nec.
            if exists(ldir): unlink(ldir)
            makedirs(ldir)
        return path
    
    def download(self):  # download web page
        try:
            retval = urlretrieve(self.url, self.file)
        except IOError:
            retval = ('*** ERROR: invalid URL "%s"' % self.url)
        return retval
        
    def parseAndGetLinks(self):  # parse HTML, save links
        self.parser = HTMLParser(AbstractFormatter(DumbWriter(StringIO())))
        self.parser.feed(open(self.file).read())
        self.parser.close()
        return self.parser.anchorlist
        

class Crawler(object):  # manage entrie crawling process
    
    count = 0  # static downloaded page counter
    
    def __init__(self, url):
        self.q = [url]
        self.seen = []
        self.dom = urlparse(url)[1]
        print 'self.dom: ', self.dom
        
    def getPage(self, url):
        r = Retriever(url)
        retval = r.download()
        if retval[0] == '*':  # error situation, do not parse 对于上面54行的错误字符串
            print retval, '... skipping parse'
            return
        Crawler.count += 1
        print '\n(', Crawler.count, ')'
        print 'URL:', url
        print 'FILE:', retval[0]
        self.seen.append(url)
         
        links = r.parseAndGetLinks()  # get and process links
        for eachLink in links:
            if eachLink[:4] != 'http' and find(eachLink, '://') == -1:
                eachLink = urljoin(url, eachLink)
            print '* ', eachLink
            
            # 如果发现有邮箱地址连接
            if find(lower(eachLink), 'mailto:') != -1:
                print '... discarded, mailto link'
                continue
            
            if eachLink not in self.seen:
                if find(eachLink, self.dom) == -1:
                    print '... discarded, not in domain'
                else:
                    if eachLink not in self.q:
                        self.q.append(eachLink)
                        print '... new, added to Q'
                    else:
                        print '... discarded, already in Q'

    def go(self):  # process links in queue
        while self.q:
            url = self.q.pop()
            self.getPage(url)
                
                
def main():
    try:
        # url = input('Enter starting URL: ')
        url = "http://baike.baidu.com/subview/2202550/11243904.htm"
    except(KeyboardInterrupt, EOFError):
        url = ''
    
    if not url:
        return
#     robot = Crawler('http://baike.baidu.com/subview/2202550/11243904.htm')
    robot = Crawler(url)
    robot.go()
    print 'Done!'
        
        
if __name__ == '__main__':
    main()
