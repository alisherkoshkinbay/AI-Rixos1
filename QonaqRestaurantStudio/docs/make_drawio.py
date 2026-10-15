"""Rebuild the editable diagrams.net diagram from the Graphviz DOT source."""
import shlex
import subprocess
import xml.etree.ElementTree as ET
from pathlib import Path

here=Path(__file__).resolve().parent
lines=subprocess.check_output(['dot','-Tplain',str(here/'UML-Architecture.dot')],text=True).replace('\\\n','').splitlines()
records=[shlex.split(line) for line in lines]
graph=next(x for x in records if x[0]=='graph')
canvas_height=float(graph[3]);scale=106;offset=90

def coords(x,y):return (round(float(x)*scale+offset),round((canvas_height-float(y))*scale+offset))

mxfile=ET.Element('mxfile',{'host':'app.diagrams.net','modified':'2026-10-15T12:00:00.000Z','agent':'QONAQ','version':'24.7.17','type':'device'})
diagram=ET.SubElement(mxfile,'diagram',{'id':'qonaq-uml','name':'Restaurant Studio UML'})
model=ET.SubElement(diagram,'mxGraphModel',{'dx':'1700','dy':'1000','grid':'1','gridSize':'10','guides':'1','tooltips':'1','connect':'1','arrows':'1','fold':'1','page':'1','pageScale':'1','pageWidth':'2100','pageHeight':'1650','math':'0','shadow':'0'})
root=ET.SubElement(model,'root')
ET.SubElement(root,'mxCell',{'id':'0'})
ET.SubElement(root,'mxCell',{'id':'1','parent':'0'})
title=ET.SubElement(root,'mxCell',{'id':'title','value':'QONAQ RESTAURANT STUDIO — JAVA 17 / UML CLASS DIAGRAM','style':'text;html=1;align=center;verticalAlign=middle;resizable=0;fontFamily=Arial;fontSize=26;fontStyle=1;fontColor=#27213C;','vertex':'1','parent':'1'})
ET.SubElement(title,'mxGeometry',{'x':'170','y':'15','width':'1250','height':'55','as':'geometry'})
labels={
    'UI':('RestaurantFrame',['+ navigate(page)','+ flash(message)']),
    'Facade':('RestaurantFacade «Facade»',['+ catalog(): List','+ addToCart(id, qty)','+ checkout(...): Order','+ advance(id)','+ cancel(id)','+ revenue(): int']),
    'Factory':('MenuFactory «abstract»',['# create(...): MenuItem','+ catalog(): List']),
    'Food':('FoodFactory',['+ catalog(): List']),
    'Drink':('DrinkFactory',['+ catalog(): List']),
    'Dessert':('DessertFactory',['+ catalog(): List']),
    'Item':('MenuItem «record»',['id, name, category','price, icon, artStyle']),
    'Builder':('OrderBuilder «Builder»',['+ customer(value)','+ phone(value)','+ addItem(line)','+ discount(value)','+ build(): Order']),
    'Order':('Order',['id, customer, items','subtotal, discount, status','+ next()','+ cancel()']),
    'State':('OrderState «interface»',['+ advance(order)','+ cancel(order)']),
    'New':('NewState',['+ advance()','+ cancel()']),
    'Preparing':('PreparingState',['+ advance()','+ cancel()']),
    'Ready':('ReadyState',['+ advance()','+ cancel()']),
    'Final':('FinalState',['+ advance()','+ cancel()']),
    'Events':('OrderEvents «Subject»',['+ subscribe(observer)','+ unsubscribe(observer)','+ publish(order, text)']),
    'Observer':('OrderObserver «interface»',['+ onOrderChanged(order, text)']),
    'Repo':('OrderRepository',['+ load(): List','+ save(orders)','+ file(): Path']),
    'Line':('CartLine «record»',['productId, name','unitPrice, quantity, note'])
}
pattern_map={'Factory':'Factory Method','Builder':'Builder','State':'State','Events':'Observer','Facade':'Facade'}
for fields in records:
    if fields[0]!='node':continue
    _,id,cx,cy,ww,hh,*_=fields
    label,methods=labels[id]
    x,y=coords(float(cx)-float(ww)/2,float(cy)+float(hh)/2)
    w=round(float(ww)*scale);h=round(float(hh)*scale)
    header=f'<b>{label}</b>'
    if id in pattern_map:header+=f'<br/><font color="#7457F6" style="font-size:10px">{pattern_map[id].upper()}</font>'
    html=f'<div style="padding:2px">{header}<hr/>'+''.join(f'<div style="text-align:left;white-space:nowrap;padding:2px 4px">{s}</div>' for s in methods)+'</div>'
    import html as html_module
    fill='#E8E0FF' if id=='Facade' else ('#F0EAFF' if id=='UI' else '#FFFFFF')
    cell=ET.SubElement(root,'mxCell',{'id':id,'value':html,'style':f'rounded=1;whiteSpace=wrap;html=1;fillColor={fill};strokeColor=#CFC7E9;fontColor=#25203C;fontSize=12;fontFamily=Arial;align=center;verticalAlign=middle;arcSize=12;spacing=7;shadow=1;','vertex':'1','parent':'1'})
    ET.SubElement(cell,'mxGeometry',{'x':str(x),'y':str(y),'width':str(w),'height':str(h),'as':'geometry'})
for count,fields in enumerate([f for f in records if f[0]=='edge'],1):
    _,start,end,n,*other=fields
    n=int(n)
    coords_string=other[:2*n];rest=other[2*n:]
    label=''
    if len(rest)>2 and not rest[0].startswith('#') and rest[0] not in ('solid','dotted','dashed'):
        label=rest[0]
    inheritance=(end in ['Factory','State'] and start in ['Food','Drink','Dessert','New','Preparing','Ready','Final'])
    style='edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;strokeWidth=2;strokeColor=#8F7BCC;fontColor=#5C5296;fontSize=11;'
    if inheritance:style+='endArrow=block;endFill=0;'
    else:style+='endArrow=classic;endFill=1;'
    if 'Observer'==end or 'Item'==end:style+='dashed=1;'
    cell=ET.SubElement(root,'mxCell',{'id':f'edge{count}','value':label,'style':style,'edge':'1','parent':'1','source':start,'target':end})
    ET.SubElement(cell,'mxGeometry',{'relative':'1','as':'geometry'})
ET.indent(mxfile,space='  ')
ET.ElementTree(mxfile).write(here/'UML-Architecture.drawio',encoding='utf-8',xml_declaration=True)
print('Created',here/'UML-Architecture.drawio')
