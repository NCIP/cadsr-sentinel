/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/list.js,v 1.6 2008-12-01 20:57:38 hebell Exp $
   $Name: not supported by cvs2svn $
*/

    var checkCount = 0;
    var lastSort = null;
    var lastSortCol = 0;
    var sortChar = "";
    var lastSortOrder = true;

    function showDebug(obj)
    {
        var d1 = document.getElementById("debugText");
        d1.style.display = "block";
        d1.textContent = showObjTree(obj);
    }

    function showObjTree(obj)
    {
        var txt = "";
        for (var indx=0; indx < obj.childNodes.length; ++indx)
        {
            txt = txt + showObjTree(obj.childNodes[indx]);
        }
        if (obj.nodeValue !== null)
        {
            txt = obj.nodeValue + " " + txt;
        }
        return "<" + obj.nodeName + ">" + txt + "</" + obj.nodeName + ">";
    }

    function loaded()
    {
        disableButs();
        stripeTable();
        var obj1 = document.getElementById("theList");
        var obj2;
        var obj3;
        var obj4;
        var obj5;
        if (obj1.children)
        {
            obj2 = obj1.children[1];
            obj3 = obj2.children[0];
            obj4 = obj3.children[1];
            obj5 = obj4.children[1];
        }
        else
        {
            obj2 = obj1.childNodes[2];
            obj3 = obj2.childNodes[0];
            obj4 = obj3.childNodes[3];
            obj5 = obj4.childNodes[1];
        }
        sortCol(obj5, 1);
        saved();
    }

    function cmdHelp()
    {
        window.open(helpUrl, "_blank");
    }

    function cmdCreate()
    {
        listForm.nextScreen.value = "create";
        listForm.submit();
    }

    function cmdDelete()
    {
        var delCount = 0;
        var delNames = "";
        var obj1 = document.getElementById("theList");
        var obj2;
        var obj3;
        var obj4;
        var obj5;
        var indx2;
        if (obj1.children)
        {
            obj2 = obj1.children[1];
            for (indx2 = 1; indx2 < obj2.children.length; ++indx2)
            {
                obj3 = obj2.children[indx2];
                obj4 = obj3.children[0];
                obj5 = obj4.children[0];
                if (obj5.checked === true)
                {
                    obj5 = obj3.children[1];
                    ++delCount;
                    delNames = delNames + ', "' + obj5.innerText + '"';
                }
            }
        }
        else
        {
            obj2 = obj1.childNodes[1];
            for (indx2 = 1; indx2 < obj2.childNodes.length; ++indx2)
            {
                obj3 = obj2.childNodes[indx2];
                obj4 = obj3.childNodes[0];
                obj5 = obj4.childNodes[0];
                if (obj5.checked === true)
                {
                    obj5 = obj3.childNodes[1];
                    ++delCount;
                    delNames = delNames + ', "' + obj5.textContent + '"';
                }
            }
        }
        if (window.confirm("Are you sure you wish to delete the " + delCount + " sentinels " + delNames.substr(2) + "?"))
        {
            listForm.nextScreen.value = "delete";
            listForm.submit();
        }
    }

    function cmdRun()
    {
        listForm.nextScreen.value = "run";
        listForm.submit();
    }

    function cmdEdit()
    {
        listForm.nextScreen.value = "edit";
        listForm.submit();
    }

    function cmdNewFrom()
    {
        listForm.nextScreen.value = "newfrom";
        listForm.submit();
    }

    function stripeTable()
    {
        var obj1 = document.getElementById("theList");
        var obj2;
        var obj3;
        var indx2;
        var scolor;
        var flip;

        if (obj1.children)
        {
            obj2 = obj1.children[1];
            scolor = "#dddddd";
            flip = false;
            for (indx2 = 0; indx2 < obj2.children.length; ++indx2)
            {
                obj3 = obj2.children[indx2];
                obj3.style.backgroundColor = scolor;
                scolor = (flip) ? "#e0f0ff" : "#ffffff";
                flip = !flip;
            }
        }
        else
        {
            obj2 = obj1.childNodes[2];
            scolor = "#dddddd";
            flip = false;
            for (indx2 = 0; indx2 < obj2.childNodes.length; ++indx2)
            {
                obj3 = obj2.childNodes[indx2];
                if (obj3.innerHTML)
                {
                    obj3.style.backgroundColor = scolor;
                    scolor = (flip) ? "#e0f0ff" : "#ffffff";
                    flip = !flip;
                }
            }
        }
    }

    function setAllChecks()
    {
        var obj1 = document.getElementById("theList");
        var obj2;
        var obj3;
        var obj4;
        var obj5;
        var flag;
        var indx2;
        
        if (obj1.children)
        {
            obj2 = obj1.children[1];
            obj3 = obj2.children[0];
            obj4 = obj3.children[0];
            obj5 = obj4.children[0];
            flag = obj5.checked;
            for (indx2 = 1; indx2 < obj2.children.length; ++indx2)
            {
                obj3 = obj2.children[indx2];
                obj4 = obj3.children[0];
                obj5 = obj4.children[0];
                obj5.checked = flag;
            }
        }
        else
        {
            obj2 = obj1.childNodes[1];
            obj3 = obj2.childNodes[0];
            obj4 = obj3.childNodes[0];
            obj5 = obj4.childNodes[0];
            flag = obj5.checked;
            for (indx2 = 1; indx2 < obj2.childNodes.length; ++indx2)
            {
                obj3 = obj2.childNodes[indx2];
                obj4 = obj3.childNodes[0];
                obj5 = obj4.childNodes[0];
                obj5.checked = flag;
            }
        }
        checkCount = (flag) ? indx2 - 1 : 0;
        fixButs2();
    }

    function setDisabled(butObj)
    {
        if (butObj.nodeName != "INPUT")
        {
            return;
        }
        else if (butObj.getAttribute("cstTestSingle") !== null)
        {
            butObj.disabled = (checkCount == 1) ? false : true;
        }
        else if (butObj.getAttribute("cstTestMulti") !== null)
        {
            butObj.disabled = (checkCount === 0) ? true : false;
        }
        else
        {
            butObj.disabled = false;
        }
    }

    function disableButs()
    {
        var cmdButsTop = document.getElementById("cmdButsTop");
        var cmdButsBtm = document.getElementById("cmdButsBtm");
        for (var index = 0; index < cmdButsTop.childNodes.length; ++index)
        {
            setDisabled(cmdButsTop.childNodes[index]);
            setDisabled(cmdButsBtm.childNodes[index]);
        }
    }

    function fixButs(butObj)
    {
        if (butObj.checked === true)
        {
            ++checkCount;
        }
        else
        {
            --checkCount;
        }
        var obj1 = document.getElementById("theList");
        var obj2;
        var obj3;
        var obj4;
        var obj5;
        var obj2Len;

        if (obj1.children)
        {
            obj2 = obj1.children[1];
            obj3 = obj2.children[0];
            obj4 = obj3.children[0];
            obj5 = obj4.children[0];
            obj2Len = obj2.children.length;
        }
        else
        {
            obj2 = obj1.childNodes[2];
            obj3 = obj2.childNodes[0];
            obj4 = obj3.childNodes[1];
            obj5 = obj4.childNodes[0];
            obj2Len = obj2.childNodes.length;
        }
        if (checkCount === 0)
        {
	        obj5.checked = false;
        }
        else if (checkCount == (obj2Len - 1))
        {
            obj5.checked = true;
        }
            
        fixButs2();
    }

    function fixButs2()
    {
        disableButs();

        var obj1 = document.getElementById("theList");
        var obj2;
        var obj3;
        var obj4;
        var obj5;
        var indx2;
        if (obj1.children)
        {
            obj2 = obj1.children[1];
            for (indx2 = 1; indx2 < obj2.children.length; ++indx2)
            {
                obj3 = obj2.children[indx2];
                obj4 = obj3.children[0];
                obj5 = obj4.children[0];
                if (obj5.checked === true &&
                    obj3.children[6].innerText != Muserid && Madmin === false)
                {
                    cmdButsTop.children[1].disabled = true;
                    cmdButsBtm.children[1].disabled = true;
                    cmdButsTop.children[3].disabled = true;
                    cmdButsBtm.children[3].disabled = true;
                    cmdButsTop.children[4].disabled = true;
                    cmdButsBtm.children[4].disabled = true;
                }
            }
        }
        else
        {
            obj2 = obj1.childNodes[2].getElementsByTagName("TR");
            for (indx2 = 1; indx2 < obj2.length; ++indx2)
            {
                obj3 = obj2[indx2];
                obj4 = obj3.childNodes[1];
                obj5 = obj4.childNodes[0];
                if (obj5.checked === true &&
                    obj3.childNodes[13].textContent != Muserid && Madmin === false)
                {
                    cmdButsTop.childNodes[3].disabled = true;
                    cmdButsBtm.childNodes[3].disabled = true;
                    cmdButsTop.childNodes[7].disabled = true;
                    cmdButsBtm.childNodes[7].disabled = true;
                    cmdButsTop.childNodes[9].disabled = true;
                    cmdButsBtm.childNodes[9].disabled = true;
                }
            }
        }
    }

    function swapCols(obja, objb)
    {
        var index;
        var temp;
        if (obja.childre)
        {
            for (index = 0; index < obja.children.length; ++index)
            {
                temp = obja.children[index].innerHTML;
                obja.children[index].innerHTML = objb.children[index].innerHTML;
                objb.children[index].innerHTML = temp;
            }
        }
        else
        {
            for (index = 0; index < obja.childNodes.length; ++index)
            {
                temp = obja.childNodes[index].innerHTML;
                obja.childNodes[index].innerHTML = objb.childNodes[index].innerHTML;
                objb.childNodes[index].innerHTML = temp;
            }
        }
    }

    function sortTextA(col)
    {
        var obj1 = document.getElementById("theList");
        var obj2;
        var maxcnt;
        var loop;
        var indx2;
        var obj3a;
        var obj4a;
        var texta;
        var obj3b;
        var obj4b;
        var textb;
        if (obj1.children)
        {
            obj2 = obj1.children[1];
            maxcnt = obj2.children.length - 1;
            loop = true;
            while (loop)
            {
                loop = false;
                for (indx2 = 1; indx2 < maxcnt; ++indx2)
                {
                    obj3a = obj2.children[indx2];
                    obj4a = obj3a.children[col];
                    texta = obj4a.innerText;
                    obj3b = obj2.children[indx2 + 1];
                    obj4b = obj3b.children[col];
                    textb = obj4b.innerText;
    
                    if (texta.toUpperCase() > textb.toUpperCase())
                    {
                        loop = true;
                        swapCols(obj3a, obj3b);
                    }
                }
            }
        }
        else
        {
            obj2 = obj1.childNodes[1];
            maxcnt = obj2.childNodes.length - 1;
            loop = true;
            while (loop)
            {
                loop = false;
                for (indx2 = 1; indx2 < maxcnt; ++indx2)
                {
                    obj3a = obj2.childNodes[indx2];
                    obj4a = obj3a.childNodes[col];
                    texta = obj4a.textContent;
                    obj3b = obj2.childNodes[indx2 + 1];
                    obj4b = obj3b.childNodes[col];
                    textb = obj4b.textContent;
    
                    if (texta.toUpperCase() > textb.toUpperCase())
                    {
                        loop = true;
                        swapCols(obj3a, obj3b);
                    }
                }
            }
        }
    }

    function sortTextD(col)
    {
        var obj1 = document.getElementById("theList");
        var obj2;
        var maxcnt;
        var loop;
        var indx2;
        var obj3a;
        var obj4a;
        var texta;
        var obj3b;
        var obj4b;
        var textb;
        if (obj1.children)
        {
            obj2 = obj1.children[1];
            maxcnt = obj2.children.length - 1;
            loop = true;
            while (loop)
            {
                loop = false;
                for (indx2 = 1; indx2 < maxcnt; ++indx2)
                {
                    obj3a = obj2.children[indx2];
                    obj4a = obj3a.children[col];
                    texta = obj4a.innerText;
                    obj3b = obj2.children[indx2 + 1];
                    obj4b = obj3b.children[col];
                    textb = obj4b.innerText;
    
                    if (texta.toUpperCase() < textb.toUpperCase())
                    {
                        loop = true;
                        swapCols(obj3a, obj3b);
                    }
                }
            }
        }
        else
        {
            obj2 = obj1.childNodes[1];
            maxcnt = obj2.childNodes.length - 1;
            loop = true;
            while (loop)
            {
                loop = false;
                for (indx2 = 1; indx2 < maxcnt; ++indx2)
                {
                    obj3a = obj2.childNodes[indx2];
                    obj4a = obj3a.childNodes[col];
                    texta = obj4a.textContent;
                    obj3b = obj2.childNodes[indx2 + 1];
                    obj4b = obj3b.childNodes[col];
                    textb = obj4b.textContent;
    
                    if (texta.toUpperCase() < textb.toUpperCase())
                    {
                        loop = true;
                        swapCols(obj3a, obj3b);
                    }
                }
            }
        }
    }

    function sortText(col)
    {
        if (col == lastSortCol)
        {
            if (lastSortOrder === true)
            {
                sortTextD(col);
                sortChar = "&#217;";
                lastSortOrder = false;
            }
            else
            {
                sortTextA(col);
                sortChar = "&#218;";
                lastSortOrder = true;
            }
        }
        else
        {
            sortTextA(col);
            sortChar = "&#218;";
            lastSortOrder = true;
        }
        lastSortCol = col;
    }

    function setSortHeading(obj)
    {
        if (lastSort !== null)
        {
            lastSort.innerHTML = "";
        }
        obj.innerHTML = sortChar;
        lastSort = obj;
    }

    function sortCol(obj, col)
    {
        sortText(col);
        setSortHeading(obj);
    }

    function setCursor(obj, cursor)
    {
        obj.style.cursor = cursor;
    }

    function cmdLogout()
    {
        listForm.action = "/cadsrsentinel/do/logout";
        listForm.submit();
    }
