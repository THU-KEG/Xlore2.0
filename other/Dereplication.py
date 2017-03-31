# -*- coding:utf-8 -*-

import os
import os.path
import json
import copy 

# STEP:1 分离非重复/重复pages
'''
A = {}
minifw = open('minifw.result.json', 'w')
cnt = 0
def doReplace(o_p, n_p):
    old_obj = json.loads(o_p)
    new_obj = json.loads(n_p)
    
    if (old_obj['title']['h1']+old_obj['title']['h2']) != (new_obj['title']['h1']+new_obj['title']['h2']):
        if old_obj['title']['h1'] == new_obj['title']['h1']:
            if old_obj['title']['h2']=='' and new_obj['title']['h2']!='':
                return True
            elif old_obj['title']['h2']!='' and new_obj['title']['h2']=='':
                return False
        print 'Titles are not equal:', (old_obj['title']['h1']+'*'+old_obj['title']['h2']), ' && ', (new_obj['title']['h1']+'*'+new_obj['title']['h2'])
        print old_obj['synonym'], new_obj['synonym']
        print old_obj['url'], new_obj['url']
        minifw.write(o_p + '\n')
        minifw.write(n_p + '\n')
        return False
        
    try:
        if len(old_obj['statistics'])==0 and len(new_obj['statistics'])!=0:
            return True
        elif len(old_obj['statistics'])!=0 and len(new_obj['statistics'])==0:
            return False
        elif len(old_obj['statistics'])!=0 and len(new_obj['statistics'])!=0:            
            if int(old_obj['statistics']['edit_times']) < int(new_obj['statistics']['edit_times']):
                if len(old_obj['polyseme']) > len(new_obj['polyseme']):
                    print '!!!what???? len(old_obj[polyseme]) > len(new_obj[polyseme])'
                return True # Replace the old_obj
            elif int(old_obj['statistics']['edit_times']) > int(new_obj['statistics']['edit_times']):
                if len(old_obj['polyseme']) < len(new_obj['polyseme']):
                    print '!!!what???? len(old_obj[polyseme]) < len(new_obj[polyseme])'
                return False

        return False
    except Exception,e:  
        print Exception,":",e
        print old_obj['statistics'], new_obj['statistics']

fc = 100000

with open('key_page.result.json', 'r') as fr:
    for line in fr:
        page = json.loads(line.strip())
        key = page['url']
        
        if key in A:
            # print key
            # print '__+__', doReplace(A[key], line.strip())
            if doReplace(A[key], line.strip()):
                A[key] = line.strip()
        else:
            A[key] = line.strip()
            cnt += 1
            if cnt%10000 == 0:
                print '__', cnt
            # if cnt == fc:
                # break

print 'total: ', cnt

with open('key_url.result.json', 'w') as fw:
    for (key, val) in A.items():
        fw.write(val + '\n')

minifw.close()
print 'Done!:D'
'''

# STEP:2 处理重复pages：简单规则、是否同一作者、更新版本
'''
cnt = 0
tem = ''
with open('221.result.json', 'w') as fw:
    with open('minifw.result.json', 'r') as minifr:
        for line in minifr:
            if cnt%2 == 1:
                old_obj = json.loads(tem.strip())
                new_obj = json.loads(line.strip())
                if len(old_obj['statistics'])!=0 and len(new_obj['statistics'])!=0:
                    
                    if old_obj['statistics']['creator'] == new_obj['statistics']['creator']:
                        o_title = old_obj['title']['h1']+old_obj['title']['h2']
                        n_title = new_obj['title']['h1']+new_obj['title']['h2']
                        if o_title.find(u'编辑')!=-1 and n_title.find(u'编辑')==-1:
                            fw.write(line)
                        elif o_title.find(u'编辑')==-1 and n_title.find(u'编辑')!=-1:
                            fw.write(tem)
                        else:
                            if old_obj['title']['h1']!=new_obj['title']['h1'] and old_obj['title']['h2']==new_obj['title']['h2'] and new_obj['title']['h2']=='':
                                if old_obj['statistics']['edit_times'] >= new_obj['statistics']['edit_times']:
                                    fw.write(tem)
                                else:
                                    fw.write(line)
                            else:
                                fw.write(tem)
                                fw.write(line)
                    else:
                        if len(old_obj['synonym'])==0 and len(new_obj['synonym'])!=0:
                            fw.write(json.dumps(new_obj, ensure_ascii=False).encode("utf-8")+'\n')
                        elif len(old_obj['synonym'])!=0 and len(new_obj['synonym'])==0:
                            fw.write(json.dumps(old_obj, ensure_ascii=False).encode("utf-8")+'\n')
                        else:
                            fw.write(tem)
                            fw.write(line)
                else:
                    if len(old_obj['synonym'])==0 and len(new_obj['synonym'])!=0:
                        fw.write(json.dumps(new_obj, ensure_ascii=False).encode("utf-8")+'\n')
                    elif len(old_obj['synonym'])!=0 and len(new_obj['synonym'])==0:
                        fw.write(json.dumps(old_obj, ensure_ascii=False).encode("utf-8")+'\n')
                    else:
                        fw.write(tem)
                        fw.write(line)
            tem = line
            cnt += 1
'''

# STEP:3 处理重复pages：是否含多义词、更新版本
'''
A = {}
with open('221.result.json', 'r') as fr:
    for line in fr:
        page = json.loads(line.strip())
        key = page['title']['h1'] + page['title']['h2']
        if key in A:
            o_obj = json.loads(A[key].strip())
            n_obj = json.loads(line.strip())
            if len(o_obj['synonym']) > len(n_obj['synonym']):
                pass
            elif len(o_obj['synonym']) < len(n_obj['synonym']):
                A[key] = line
            elif len(o_obj['synonym'])==0 and len(n_obj['synonym'])==0:
                if len(o_obj['statistics'])!=0 and len(n_obj['statistics'])!=0:
                    if o_obj['statistics']['pv'] > n_obj['statistics']['pv']:
                        pass
                    else:
                        A[key] = line
                else:
                    pass
        else:
            A[key] = line

print 'len', len(A)

with open('221sub.result.json', 'w') as fw:
    for (key, val) in A.items():
        fw.write(val)
'''

'''
# STEP:4 处理重复pages：pages 去重 与 url去重数量对比
A = []
B = []
cnt = 0
with open('221sub.result.json', 'r') as fr:
    for line in fr:
        A.append(line)
        page = json.loads(line.strip())
        B.append(page['url'])

A = list(set(A))
print 'len', len(A)
B = list(set(B))
print 'B_len', len(B)

with open('221sub_rul.result.json', 'w') as fw:
    for val in A:
        fw.write(val)

with open('key_url_title.result.json', 'w') as fw:
    with open('key_url.result.json', 'r') as fr:
        for line in fr:
            page = json.loads(line.strip())
            if page['url'] in B:
                continue
            fw.write(line)
    for line in A:
        fw.write(line)

print 'Done!:D'
'''
'''
# STEP:5 依标题去重
cnt = 0
A = {}
SKI = {}
with open('key_url_title.result.json', 'r') as fr:
    for line in fr:
        page = json.loads(line.strip())
        key = page['title']['h1']+'###'+page['title']['h2']
        if len(page['content'])==0 and len(page['description'])==0 and len(page['statistics'])==0:
            continue
        if key in A:
            # print key
            o_p = json.loads(A[key].strip())
            if o_p['url'].find('/view/')==-1 and page['url'].find('/view/')!=-1:
                A[key] = line
                continue
            elif o_p['url'].find('/subview/')==-1 and page['url'].find('/subview/')!=-1:
                continue
            if len(o_p['statistics'])!=0 and len(page['statistics'])!=0:
                if o_p['statistics']['creator']==page['statistics']['creator']:
                    if len(o_p['synonym']) < len(page['synonym']):
                        A[key] = line
                    elif len(o_p['synonym']) > len(page['synonym']):
                        continue
                    elif len(o_p['synonym'])==0:
                        if o_p['statistics']['pv'] < page['statistics']['pv']:
                            A[key] = line
                    else:
                        if json.dumps(o_p['synonym'], ensure_ascii=False).encode("utf-8") != json.dumps(page['synonym'], ensure_ascii=False).encode("utf-8"):
                            o_p['synonym']['from'] = o_p['synonym']['from'] + '||' + page['synonym']['from']
                            o_p['synonym']['to'] = o_p['synonym']['to'] + '||' + page['synonym']['to']
                            A[key] = json.dumps(o_p, ensure_ascii=False).encode("utf-8") + '\n'
                        # print '\n\n***'
                        # print o_p['synonym'], page['synonym']
                        # print '\n\n'
                elif o_p['statistics']['creator']!=page['statistics']['creator']:
                    if key in SKI:
                        SKI[key].append(line)
                    else:
                        SKI[key] = []
                        SKI[key].append(line)
            elif len(o_p['statistics'])!=0 and len(page['statistics'])==0:
                if json.dumps(o_p['synonym'], ensure_ascii=False).encode("utf-8") != json.dumps(page['synonym'], ensure_ascii=False).encode("utf-8") and len(o_p['synonym'])>0 and len(page['synonym'])>0:
                    o_p['synonym']['from'] = o_p['synonym']['from'] + '||' + page['synonym']['from']
                    o_p['synonym']['to'] = o_p['synonym']['to'] + '||' + page['synonym']['to']
                    A[key] = json.dumps(o_p, ensure_ascii=False).encode("utf-8") + '\n'
            else:
                print '\n\n\n--has no creator-----'
                print A[key], line
                print o_p['url'], page['url']
                print o_p['synonym'], page['synonym']
                print o_p['statistics'], page['statistics']
                print o_p['polyseme'], page['polyseme']
                print '------------------'
                aaa = input()
        else:
            A[ key ] = line
            cnt += 1
            if cnt%10000 == 0:
                print '__cnt:', cnt

for (key, arr) in SKI.items():
    o_p = json.loads(A[key])
    tem = 0
    if o_p['title']['h2'] == '':
        o_p['title']['h2'] = str(tem).zfill(6)
        A[o_p['title']['h1']+'###'+o_p['title']['h2']] = json.dumps(o_p, ensure_ascii=False).encode("utf-8") + '\n'
        tem += 1
        for line in arr:
            page = json.loads(line.strip())
            page['title']['h2'] = str(tem).zfill(6)
            A[page['title']['h1']+'###'+page['title']['h2']] = json.dumps(page, ensure_ascii=False).encode("utf-8") + '\n'
            tem += 1
        del A[key]
        
print 'len:', len(A)

with open('key_url_title_final.result.json', 'w') as fw:
    for (key, val) in A.items():
        fw.write(val)

print 'Done!:D'
'''
