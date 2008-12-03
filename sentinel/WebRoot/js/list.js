/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/list.js,v 1.7 2008-12-03 00:16:27 hebell Exp $
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
        var sObj = document.getElementById("cName");
        sortCol(sObj, 1);
        saved();
    }

    function cmdHelp()
    {
        window.open(helpUrl, "_blank");
    }

    function cmdCreate()
    {
        var fld = document.getElementsByName("nextScreen");
        fld[0].value = "create";
        fld = document.getElementsByName("listForm");
        fld[0].submit();
    }

    function cmdDelete()
    {
        var delCount = 0;
        var delNames = "";
        var cbs;
        var tds;
        var aName;
        var indx2;
        var table = document.getElementById("theList");
        var trs = table.getElementsByTagName("TR");

        for (indx2 = 1; indx2 < trs.length; ++indx2)
        {
            cbs = trs[indx2].getElementsByTagName("INPUT");
            if (cbs[0].checked === true)
            {
                tds = trs[indx2].getElementsByTagName("TD");
                aName = (tds[1].innerText) ? tds[1].innerText : tds[1].textContent;
                ++delCount;
                delNames = delNames + ', "' + aName + '"';
            }
        }
        
        if (window.confirm("Are you sure you wish to delete the " + delCount + " sentinels " + delNames.substr(2) + "?"))
        {
            tds = document.getElementsByName("nextScreen");
            tds[0].value = "delete";
            tds = document.getElementsByName("listForm");
            tds[0].submit();
        }
    }

    function cmdRun()
    {
        var fld = document.getElementsByName("nextScreen");
        fld[0].value = "run";
        fld = document.getElementsByName("listForm");
        fld[0].submit();
    }

    function cmdEdit()
    {
        var fld = document.getElementsByName("nextScreen");
        fld[0].value = "edit";
        fld = document.getElementsByName("listForm");
        fld[0].submit();
    }

    function cmdNewFrom()
    {
        var fld = document.getElementsByName("nextScreen");
        fld[0].value = "newfrom";
        fld = document.getElementsByName("listForm");
        fld[0].submit();
    }

    function stripeTable()
    {
        var table = document.getElementById("theList");
        var trs = table.getElementsByTagName("TR");
        var scolor = "#dddddd";
        var flip = false;

        for (var indx2 = 0; indx2 < trs.length; ++indx2)
        {
            trs[indx2].style.backgroundColor = scolor;
            scolor = (flip) ? "#e0f0ff" : "#ffffff";
            flip = !flip;
        }
    }

    function setAllChecks()
    {
        var table = document.getElementById("theList");
        var trs = table.getElementsByTagName("TR");
        var masterCB = trs[0].getElementsByTagName("INPUT");
        var masterFlag = masterCB[0].checked;
        var oneCB;
        var cnt;

        for (cnt = 1; cnt < trs.length; ++cnt)
        {
            oneCB = trs[cnt].getElementsByTagName("INPUT");
            oneCB[0].checked = masterFlag;
        }
        checkCount = (masterFlag) ? cnt - 1 : 0;
        fixButs2();
    }

    function setDisabled(butObj)
    {
        if (butObj.getAttribute("cstTestSingle") !== null)
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
        var table = document.getElementById("cmdButsTop");
        var cmdButsTop = table.getElementsByTagName("INPUT");
        table = document.getElementById("cmdButsBtm");
        var cmdButsBtm = table.getElementsByTagName("INPUT");
        for (var index = 0; index < cmdButsTop.length; ++index)
        {
            setDisabled(cmdButsTop[index]);
            setDisabled(cmdButsBtm[index]);
        }
    }

    function fixButs(butObj)
    {
        if (butObj.checked === true)
        {
            // Increment check count
            ++checkCount;
        }
        else
        {
            // Decrement check count
            --checkCount;
        }
        
        var table = document.getElementById("theList");
        var trs = table.getElementsByTagName("TR");
        var cbs = trs[0].getElementsByTagName("INPUT");
        
        if (checkCount === 0)
        {
            // If the check count is zero be sure the global check box is off
            cbs[0].checked = false;
        }
        else if (checkCount == (trs.length - 1))
        {
            // If the check count is the same as the number of data rows be sure
            // the global check box is on
            cbs[0].checked = true;
        }

        // Fix the button enable/disable based on the check count            
        fixButs2();
    }

    function fixButs2()
    {
        disableButs();

        var table;
        
        table = document.getElementById("cmdButsTop");
        var cmdButsTop = table.getElementsByTagName("INPUT");
        table = document.getElementById("cmdButsBtm");
        var cmdButsBtm = table.getElementsByTagName("INPUT");

        table = document.getElementById("theList");
        var trs = table.getElementsByTagName("TR");
        var cbs;
        var tds;
        var aName;
        for (var cnt = 1; cnt < trs.length; ++cnt)
        {
            tds = trs[cnt].getElementsByTagName("TD");
            aName = (tds[6].innerText) ? tds[6].innerText : tds[6].textContent;
            cbs = trs[cnt].getElementsByTagName("INPUT");
            if (cbs[0].checked === true && aName != Muserid && Madmin === false)
            {
                cmdButsTop[1].disabled = true;
                cmdButsTop[3].disabled = true;
                cmdButsTop[4].disabled = true;

                cmdButsBtm[1].disabled = true;
                cmdButsBtm[3].disabled = true;
                cmdButsBtm[4].disabled = true;
            }
        }
    }
    
    function copyRow(cols)
    {
        var tCols = new Array();
        for (var cnt = 0; cnt < cols.length; ++cnt)
        {
            tCols[cnt] = cols[cnt].innerHTML;
        }

        return tCols;
    }
    
    function pasteRow(cols, tCols)
    {
        for (var cnt = 0; cnt < cols.length; ++cnt)
        {
            cols[cnt].innerHTML = tCols[cnt];
        }
    }
    
    function orderList(col, order, sType)
    {
        var table = document.getElementById("theList");
        var trs = table.getElementsByTagName("TR");
        var tds;
        var trsNdx = new Array();
        var trsTxt = new Array();
        var mn;
        var mx;
        var mp;
        var newTxt;
        var found;
        var cnt;
        var img;
        var tTable = new Array();
        
        for (cnt = 1; cnt < trs.length; ++cnt)
        {
            tds = trs[cnt].getElementsByTagName("TD");
            tTable[cnt] = copyRow(tds);

            if (sType == "txt")
            {
                newTxt = (tds[col].innerText) ? tds[col].innerText : tds[col].textContent;
            }
            else
            {
                img = tds[col].getElementsByTagName("IMG");
                newTxt = img[0].getAttribute("cstSortKey");
            }
            newTxt = newTxt.toLowerCase();
            
            if (trsNdx.length === 0)
            {
                trsTxt[0] = newTxt;
                trsNdx[0] = cnt;
                continue;
            }
            
            mn = 0;
            mx = trsNdx.length;
            while (true)
            {
                mp = Math.floor((mn + mx) / 2);
                
                found = order * newTxt.localeCompare(trsTxt[mp]);

                if (found === 0)
                {
                    break;
                }                
                else if (found > 0)
                {
                    if (mn == mp)
                    {
                        if (order > 0)
                        {
                            ++mp;
                        }
                        break;
                    }
                    mn = mp;
                }
                else
                {
                    if (mx == mp)
                    {
                        break;
                    }
                    mx = mp;
                }
            }
            
            trsNdx.splice(mp, 0, cnt);
            trsTxt.splice(mp, 0, newTxt);
        }
        
        for (cnt = 0; cnt < trsNdx.length; ++cnt)
        {
            mn = cnt + 1;
            mx = trsNdx[cnt];
            if (mn != mx)
            {
                tds = trs[mn].getElementsByTagName("TD");
                pasteRow(tds, tTable[mx]);
            }
        }
    }

    function setSortHeading(obj)
    {
        if (lastSort !== null)
        {
            lastSort.src = "../images/blank.gif";
        }
        lastSort = obj;
        lastSort.src = "../images/" + sortChar + ".gif";
    }
    
    function setSortGlyph(col)
    {
        if (col == lastSortCol)
        {
            if (lastSortOrder === true)
            {
                sortChar = "arrow_16_down";
                lastSortOrder = false;
            }
            else
            {
                sortChar = "arrow_16_up";
                lastSortOrder = true;
            }
        }
        else
        {
            sortChar = "arrow_16_up";
            lastSortOrder = true;
        }
        lastSortCol = col;
    }

    function sortCol(obj, col)
    {
        var sortType = (obj.id == "cStatus") ? "img" : "txt";

        setSortGlyph(col);
        
        orderList(col, (lastSortOrder) ? 1 : -1, sortType);
        
        setSortHeading(obj);
    }

    function setCursor(obj, cursor)
    {
        obj.style.cursor = cursor;
    }

    function cmdLogout()
    {
        var form = document.getElementsByName("listForm");
        form[0].action = "/cadsrsentinel/do/logout";
        form[0].submit();
    }
