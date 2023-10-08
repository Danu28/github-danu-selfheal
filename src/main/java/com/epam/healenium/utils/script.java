package com.epam.healenium.utils;

public interface script {

    String jsScript = "var items = [];\n" +
            "var a = arguments[0];\n" +
            "while (a != document) {\n" +
            "  var child = a;\n" +
            "  var i=0; while(child=child.previousElementSibling) i++;\n" +
            "  var node = {tag:null,id:null,index:null,classes:[],other:{},innerText:\"\"};\n" +
            "  node.tag = a.tagName.toLowerCase();\n" +
            "  node.id = a.id;\n" +
            "  node.index = i;\n" +
            "  node.innerText = a.innerText;\n" +
            "\n" +
            "  if (a.hasAttribute(\"class\")) {\n" +
            "\t  node.classes = a.attributes.class.value.split(' ');\n" +
            "  }\n" +
            "  for (index = 0; index < a.attributes.length; ++index) {\n" +
            "      var attrName = a.attributes[index].name;\n" +
            "      if ([\"id\",\"class\"].indexOf(attrName) == -1){\n" +
            "\t\t    node.other[attrName] = a.attributes[index].value;\n" +
            "      }\n" +
            "  };\n" +
            "\n" +
            "  items.unshift(node);\n" +
            "  a = a.parentNode;\n" +
            "}\n" +
            "return JSON.stringify(items);";

    String skippedAttribute =
            "\n" +
            "style\n" +
            "onChange\n" +
            "onKeyPress\n" +
            "onSubmit\n" +
            "onReset\n" +
            "onClick\n" +
            "onFocus\n" +
            "onLoad\n" +
            "media\n" +
            "height\n" +
            "width\n" +
            "formenctype\n" +
            "formnovalidate";

    String index =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>SHA</title>\n" +
            "    <link href=\"https://fonts.googleapis.com/css?family=Roboto&display=swap\" rel=\"stylesheet\">\n" +
            "    <link href=\"https://fonts.googleapis.com/css?family=Roboto+Mono&display=swap\" rel=\"stylesheet\">\n" +
            "    <style>\n" +
            "        * {\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "            border: none;\n" +
            "        }\n" +
            "\n" +
            "        .content-wrapper {\n" +
            "            max-width: 1136px;\n" +
            "            width: 100%;\n" +
            "            height: 100%;\n" +
            "            display: flex;\n" +
            "            align-items: center;\n" +
            "            margin: 0 auto;\n" +
            "        }\n" +
            "\n" +
            "        .header {\n" +
            "            width: 100%;\n" +
            "            height: 60px;\n" +
            "            background-color: #f8f8f8;\n" +
            "        }\n" +
            "\n" +
            "        .logo-title {\n" +
            "            margin-left: 10px;\n" +
            "            font-size: 18px;\n" +
            "            font-family: Roboto, sans-serif;\n" +
            "            font-weight: 600;\n" +
            "            color: #000000;\n" +
            "        }\n" +
            "\n" +
            "        .main-area {\n" +
            "            display: flex;\n" +
            "            flex-direction: column;\n" +
            "            width: 100%;\n" +
            "        }\n" +
            "\n" +
            "        .report-info {\n" +
            "            margin: 30px 0;\n" +
            "            font-size: 24px;\n" +
            "            font-family: Roboto, sans-serif;\n" +
            "            font-weight: 600;\n" +
            "            color: #000000;\n" +
            "        }\n" +
            "\n" +
            "        .element-column {\n" +
            "            flex: 6 0 10%;\n" +
            "        }\n" +
            "\n" +
            "        .screenshot-column {\n" +
            "            flex: 2 0 10%;\n" +
            "        }\n" +
            "\n" +
            "        .actions-column {\n" +
            "            flex: 1 0 10%;\n" +
            "            text-align: right;\n" +
            "        }\n" +
            "\n" +
            "        .elements-table-header {\n" +
            "            display: flex;\n" +
            "            padding-bottom: 8px;\n" +
            "            border-bottom: 1px solid #f2f2f4;\n" +
            "            font-size: 16px;\n" +
            "            font-family: Roboto, sans-serif;\n" +
            "            font-weight: 600;\n" +
            "            color: #9b9b9b;\n" +
            "        }\n" +
            "\n" +
            "        .elements-table-row {\n" +
            "            min-height: 47px;\n" +
            "            box-sizing: border-box;\n" +
            "            padding: 15px;\n" +
            "            border-bottom: 1px solid #f2f2f4;\n" +
            "        }\n" +
            "        .elements-table-row.Accepted {\n" +
            "            background-color: #f4fff4;\n" +
            "        }\n" +
            "        .elements-table-row.Accepted .row-status {\n" +
            "            color: #5cb85c;\n" +
            "        }\n" +
            "        .elements-table-row.Denied {\n" +
            "            background-color: #fff7f7;\n" +
            "        }\n" +
            "        .elements-table-row.Denied .row-status {\n" +
            "            color: #d9534f;\n" +
            "        }\n" +
            "        .elements-table-row svg {\n" +
            "            transform: rotate(90deg);\n" +
            "        }\n" +
            "        .elements-table-row.content-hidden .row-content {\n" +
            "            display: none;\n" +
            "        }\n" +
            "        .elements-table-row.content-hidden svg {\n" +
            "            transform: none;\n" +
            "        }\n" +
            "\n" +
            "        .row-caption {\n" +
            "            position: relative;\n" +
            "            display: flex;\n" +
            "            align-items: center;\n" +
            "            cursor: pointer;\n" +
            "        }\n" +
            "\n" +
            "        .row-status {\n" +
            "            position: absolute;\n" +
            "            right: 0;\n" +
            "            font-size: 14px;\n" +
            "            font-family: Roboto, sans-serif;\n" +
            "            font-weight: 600;\n" +
            "        }\n" +
            "\n" +
            "        .row-caption-text {\n" +
            "            max-width: 90%;\n" +
            "            margin-left: 5px;\n" +
            "            overflow: hidden;\n" +
            "            font-size: 13px;\n" +
            "            font-family: 'Roboto Mono', monospace;\n" +
            "            font-weight: bold;\n" +
            "            text-overflow: ellipsis;\n" +
            "        }\n" +
            "\n" +
            "        .row-content {\n" +
            "            display: flex;\n" +
            "            margin-top: 5px;\n" +
            "        }\n" +
            "        .row-content.hidden {\n" +
            "            display: none;\n" +
            "        }\n" +
            "        .row-content .element-column {\n" +
            "            margin: 10px 0 0 30px;\n" +
            "        }\n" +
            "\n" +
            "        .expand-control {\n" +
            "            margin-right: 5px;\n" +
            "        }\n" +
            "\n" +
            "        .screenshot-image {\n" +
            "            width: 141px;\n" +
            "            height: 88px;\n" +
            "            box-shadow: 0 2px 24px 0 rgba(0, 0, 0, 0.09);\n" +
            "        }\n" +
            "\n" +
            "        .element-field-holder {\n" +
            "            padding-bottom: 5px;\n" +
            "            font-family: 'Roboto Mono', monospace;\n" +
            "            font-size: 13px;\n" +
            "            letter-spacing: -0.21px;\n" +
            "        }\n" +
            "\n" +
            "        .action-button {\n" +
            "            display: inline-block;\n" +
            "            width: 92px;\n" +
            "            height: 36px;\n" +
            "            margin-bottom: 10px;\n" +
            "            border-radius: 4px;\n" +
            "            color: #ffffff;\n" +
            "            cursor: pointer;\n" +
            "        }\n" +
            "        .action-button:not(:last-child) {\n" +
            "            margin-right: 5px;\n" +
            "        }\n" +
            "        .action-button.positive {\n" +
            "            background-color: #5cb85c;\n" +
            "        }\n" +
            "        .action-button.negative {\n" +
            "            background-color: #d9534f;\n" +
            "        }\n" +
            "\t\t\n" +
            "        .change-status-button {\n" +
            "            margin-top: 10px;\n" +
            "            color: #0091ff;\n" +
            "            background: none;\n" +
            "            font-size: 14px;\n" +
            "            font-family: Roboto, sans-serif;\n" +
            "            line-height: 1.14;\n" +
            "            text-align: right;\n" +
            "            cursor: pointer;\n" +
            "        }\n" +
            "\n" +
            "        #myImg {\n" +
            "          border-radius: 5px;\n" +
            "          cursor: pointer;\n" +
            "          transition: 0.3s;\n" +
            "        }\n" +
            "\n" +
            "        #myImg:hover {opacity: 0.7;}\n" +
            "\n" +
            "        .modal {\n" +
            "          display: none;\n" +
            "          position: fixed;\n" +
            "          z-index: 1;\n" +
            "          padding-top: 100px;\n" +
            "          left: 0;\n" +
            "          top: 0;\n" +
            "          width: 100%;\n" +
            "          height: 100%;\n" +
            "          overflow: auto;\n" +
            "          background-color: rgb(0,0,0);\n" +
            "          background-color: rgba(0,0,0,0.9);\n" +
            "        }\n" +
            "\n" +
            "        .modal-content {\n" +
            "          margin: auto;\n" +
            "          display: block;\n" +
            "          width: 80%;\n" +
            "          max-width: 700px;\n" +
            "        }\n" +
            "\n" +
            "        #caption {\n" +
            "          margin: auto;\n" +
            "          display: block;\n" +
            "          width: 80%;\n" +
            "          max-width: 700px;\n" +
            "          text-align: center;\n" +
            "          color: #ccc;\n" +
            "          padding: 10px 0;\n" +
            "          height: 150px;\n" +
            "        }\n" +
            "\n" +
            "        .modal-content, #caption {\n" +
            "          animation-name: zoom;\n" +
            "          animation-duration: 0.6s;\n" +
            "        }\n" +
            "\n" +
            "        @keyframes zoom {\n" +
            "          from {transform:scale(0)}\n" +
            "          to {transform:scale(1)}\n" +
            "        }\n" +
            "\n" +
            "        .close {\n" +
            "          position: absolute;\n" +
            "          top: 15px;\n" +
            "          right: 35px;\n" +
            "          color: #f1f1f1;\n" +
            "          font-size: 40px;\n" +
            "          font-weight: bold;\n" +
            "          transition: 0.3s;\n" +
            "        }\n" +
            "\n" +
            "        .close:hover,\n" +
            "        .close:focus {\n" +
            "          color: #bbb;\n" +
            "          text-decoration: none;\n" +
            "          cursor: pointer;\n" +
            "        }\n" +
            "\n" +
            "        @media only screen and (max-width: 700px){\n" +
            "          .modal-content {\n" +
            "            width: 100%;\n" +
            "          }\n" +
            "        }\n" +
            "\t\t\n" +
            "\t\t/* Hide Accept and Deny buttons */\n" +
            "\t\t.action-button.positive,\n" +
            "\t\t.action-button.negative {\n" +
            "\t\t\tdisplay: none;\n" +
            "\t\t}\n" +
            "    </style>\n" +
            "</head>\n" +
            "    <body>\n" +
            "        <header class=\"header\">\n" +
            "            <div class=\"content-wrapper\">\n" +
            "                <span class=\"logo-title\">SELF HEAL</span>\n" +
            "            </div>\n" +
            "        </header>\n" +
            "        <main class=\"content-wrapper\">\n" +
            "            <div class=\"main-area\">\n" +
            "                <h2 class=\"report-info\">\n" +
            "                    <span class=\"report-name\" data-report-name></span>\n" +
            "                    <span class=\"report-time\" data-report-time></span>\n" +
            "                </h2>\n" +
            "                <div class=\"report-content\">\n" +
            "                    <div class=\"elements-table-header\">\n" +
            "                        <span class=\"element-column\">\n" +
            "                            Element\n" +
            "                        </span>\n" +
            "                        <span class=\"screenshot-column\">\n" +
            "                            Screenshot\n" +
            "                        </span>\n" +
            "                        <span class=\"actions-column\"></span>\n" +
            "                    </div>\n" +
            "                    <div data-elements-table></div>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </main>\n" +
            "    </body>\n" +
            "    <script type=\"html/tpl\" id=\"elements-list-row-template\">\n" +
            "        <div class=\"elements-table-row content-hidden {elementStatus}\">\n" +
            "            <div class=\"row-caption element-column\" data-row-index={dataIndex}>\n" +
            "                <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"8\" height=\"13\" viewBox=\"0 0 8 13\">\n" +
            "                    <path fill=\"none\" fill-rule=\"evenodd\" stroke=\"#000\" stroke-width=\"2\" d=\"M1 11.5l5-5-5-5\"/>\n" +
            "                </svg>\n" +
            "                <span class=\"row-caption-text\">\n" +
            "                    {declaringClass}\n" +
            "                </span>\n" +
            "                <span class=\"row-status\" data-status-index={dataIndex}>\n" +
            "                    {elementStatus}\n" +
            "                </span>\n" +
            "            </div>\n" +
            "            <div class=\"row-content\">\n" +
            "                <div class=\"element-column\">\n" +
            "                    {contentElements}\n" +
            "                </div>\n" +
            "                <div class=\"screenshot-column\">\n" +
            "                    <img id=\"myImg\" src={screenShotPath} style=\"width:100%;max-width:300px\" onclick=\"zoomImage(this);\">\n" +
            "                    <div id=\"myModal\" class=\"modal\">\n" +
            "                      <span class=\"close\">&times;</span>\n" +
            "                      <img class=\"modal-content\" id=\"img01\">\n" +
            "                      <div id=\"caption\"></div>\n" +
            "                    </div>\n" +
            "                </div>\n" +
            "                <div class=\"actions-column\" data-actions-index={dataIndex}>\n" +
            "                    {actionItems}\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </script>\n" +
            "    <script>\n" +
            "        const DATA_FILE_LOCATION = './data.json'; // change to your path\n" +
            "        const ITEM_ACTIONS_TO_STATUSES = {\n" +
            "          accept: 'Accepted',\n" +
            "          deny: 'Denied',\n" +
            "          change: '',\n" +
            "        };\n" +
            "\n" +
            "        let elements = [];\n" +
            "        let storageItemKey = '';\n" +
            "        let savedElementsStatuses = [];\n" +
            "\n" +
            "        function getAcceptElementUrl(fileName, oldLocator, newLocator) {\n" +
            "          return `http://localhost:8091?target=${fileName}&oldLocator=${oldLocator}&newLocator=${newLocator}`;\n" +
            "        }\n" +
            "\n" +
            "        function getStorageItem(key) {\n" +
            "          return localStorage.getItem(key) ? JSON.parse(localStorage.getItem(key)) : null;\n" +
            "        }\n" +
            "        function setStorageItem(key, value) {\n" +
            "          return localStorage.setItem(key, JSON.stringify(value));\n" +
            "        }\n" +
            "\n" +
            "        function renderTemplate(name, data = {}) {\n" +
            "          var template = document.getElementById(name).innerHTML;\n" +
            "\n" +
            "          for (var property in data) {\n" +
            "            if (data.hasOwnProperty(property)) {\n" +
            "              var search = new RegExp('{' + property + '}', 'g');\n" +
            "              template = template.replace(search, data[property]);\n" +
            "            }\n" +
            "          }\n" +
            "          return template;\n" +
            "        }\n" +
            "\n" +
            "        function toggleElementContent(targetElement) {\n" +
            "          const elementWithIndex = targetElement.hasAttribute('data-row-index') ? targetElement : targetElement.parentNode;\n" +
            "          const rowIndex = elementWithIndex.getAttribute('data-row-index');\n" +
            "          if (rowIndex) {\n" +
            "            elementWithIndex.parentNode.classList.toggle('content-hidden');\n" +
            "          }\n" +
            "        }\n" +
            "\n" +
            "        function getStatusActionsMarkup(status) {\n" +
            "          return status ?\n" +
            "            '<button class=\"change-status-button\" data-action-type=\"change\">Change</button>' :\n" +
            "            '<button class=\"action-button positive\" data-action-type=\"accept\">Accept</button>' +\n" +
            "            '<button class=\"action-button negative\" data-action-type=\"deny\">Deny</button>';\n" +
            "        }\n" +
            "\n" +
            "        function setNewItemStatus(itemIndex, newStatus, actionsBlockNode) {\n" +
            "          const statusNode = document.querySelector(`[data-status-index=\"${itemIndex}\"]`);\n" +
            "\n" +
            "          statusNode.innerText = newStatus;\n" +
            "          actionsBlockNode.innerHTML = getStatusActionsMarkup(newStatus);\n" +
            "          savedElementsStatuses[itemIndex] = newStatus;\n" +
            "          setStorageItem(storageItemKey, savedElementsStatuses)\n" +
            "        }\n" +
            "\n" +
            "        // accept selected element, send corresponding request\n" +
            "        function acceptElement(itemIndex, { rowNode, actionsBlockNode }) {\n" +
            "          const { fileName, failedLocatorValue, healedLocatorValue } = elements[itemIndex] || {};\n" +
            "          fetch(getAcceptElementUrl(fileName, failedLocatorValue, healedLocatorValue),\n" +
            "            { method: 'post', body: '' }\n" +
            "          )\n" +
            "            .then(() => {\n" +
            "              rowNode.classList.add(ITEM_ACTIONS_TO_STATUSES.accept);\n" +
            "              setNewItemStatus(itemIndex, ITEM_ACTIONS_TO_STATUSES.accept, actionsBlockNode)\n" +
            "            })\n" +
            "            .catch((error) => console.log(error));\n" +
            "        }\n" +
            "\n" +
            "        function switchItemAction(buttonType, itemIndex, actionsBlockNode) {\n" +
            "          const rowNode = document.querySelector(`[data-row-index=\"${itemIndex}\"]`).parentNode;\n" +
            "\n" +
            "          switch (buttonType) {\n" +
            "            case 'change':\n" +
            "              rowNode.classList.remove(ITEM_ACTIONS_TO_STATUSES.accept);\n" +
            "              rowNode.classList.remove(ITEM_ACTIONS_TO_STATUSES.deny);\n" +
            "              setNewItemStatus(itemIndex, ITEM_ACTIONS_TO_STATUSES.change, actionsBlockNode);\n" +
            "              break;\n" +
            "            case 'accept':\n" +
            "              acceptElement(itemIndex, { rowNode, actionsBlockNode });\n" +
            "              break;\n" +
            "            default:\n" +
            "              setNewItemStatus(itemIndex, ITEM_ACTIONS_TO_STATUSES.deny, actionsBlockNode);\n" +
            "              rowNode.classList.add(ITEM_ACTIONS_TO_STATUSES.deny);\n" +
            "          }\n" +
            "        }\n" +
            "\n" +
            "        function itemActionClickHandler(targetElement) {\n" +
            "          const actionsBlockNode = targetElement.parentNode;\n" +
            "          if (actionsBlockNode.classList.contains('actions-column')) {\n" +
            "            const buttonType = targetElement.getAttribute(\"data-action-type\");\n" +
            "            const itemIndex = parseInt(actionsBlockNode.getAttribute('data-actions-index'), 10);\n" +
            "            if (typeof itemIndex !== 'number') {\n" +
            "              return;\n" +
            "            }\n" +
            "\n" +
            "            switchItemAction(buttonType, itemIndex, actionsBlockNode);\n" +
            "          }\n" +
            "        }\n" +
            "\n" +
            "        function rowClickHandler(event) {\n" +
            "          const targetElement = event.target;\n" +
            "\n" +
            "          toggleElementContent(targetElement);\n" +
            "          itemActionClickHandler(targetElement);\n" +
            "        }\n" +
            "\n" +
            "         function zoomImage(event) {\n" +
            "            const targetElement = event;\n" +
            "            var modal = document.getElementById(\"myModal\");\n" +
            "            var img = targetElement;\n" +
            "            var modalImg = document.getElementById(\"img01\");\n" +
            "            var captionText = document.getElementById(\"caption\");\n" +
            "\n" +
            "            // When the user clicks on image open the modal\n" +
            "              modal.style.display = \"block\";\n" +
            "              modalImg.src = img.src;\n" +
            "              captionText.innerHTML = img.alt;\n" +
            "\n" +
            "            // Get the <span> element that closes the modal\n" +
            "            var span = document.getElementsByClassName(\"close\")[0];\n" +
            "\n" +
            "            // When the user clicks on <span> (x), close the modal\n" +
            "            span.onclick = function() {\n" +
            "              modal.style.display = \"none\";\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        function renderElements(elements) {\n" +
            "          const elementsTable = document.querySelector('[data-elements-table]');\n" +
            "          elementsTable.addEventListener('click', rowClickHandler);\n" +
            "          elementsTable.innerHTML = '';\n" +
            "\n" +
            "          elements.forEach((item, index) => {\n" +
            "            const { declaringClass, screenShotPath, ...fields } = item;\n" +
            "            const contentElements = Object.keys(fields).reduce((acc, fieldKey) =>\n" +
            "              acc + `<div class=\"element-field-holder\">${fieldKey} = ${fields[fieldKey]}</div>`, '');\n" +
            "            const itemStatus = savedElementsStatuses[index] || '';\n" +
            "\n" +
            "            elementsTable.innerHTML += renderTemplate('elements-list-row-template', {\n" +
            "              declaringClass: declaringClass,\n" +
            "              screenShotPath: `./${screenShotPath}`,\n" +
            "              contentElements,\n" +
            "              elementStatus: itemStatus,\n" +
            "              dataIndex: index,\n" +
            "              actionItems: getStatusActionsMarkup(itemStatus),\n" +
            "            });\n" +
            "          });\n" +
            "        }\n" +
            "\n" +
            "        function processData(data) {\n" +
            "          const reportNameElement = document.querySelector('[data-report-name]');\n" +
            "          const reportTimeElement = document.querySelector('[data-report-time]');\n" +
            "          const { reportName, endTime, elementsInfo } = data;\n" +
            "          elements = elementsInfo;\n" +
            "          storageItemKey = `${reportName}_${endTime}`;\n" +
            "\n" +
            "          reportNameElement.innerText = reportName;\n" +
            "          reportTimeElement.innerText = endTime;\n" +
            "\n" +
            "          savedElementsStatuses = getStorageItem(storageItemKey) || [];\n" +
            "\n" +
            "          renderElements(elementsInfo);\n" +
            "        }\n" +
            "\n" +
            "        function catchRequestError(error) {\n" +
            "          const elementsTable = document.querySelector('[data-elements-table]');\n" +
            "          elementsTable.innerHTML = `An error occurred while requesting data: ${error}`;\n" +
            "        }\n" +
            "\n" +
            "        function getData() {\n" +
            "          fetch(DATA_FILE_LOCATION, { responseType: 'json' })\n" +
            "            .then((response) => response.json())\n" +
            "            .then((data) => {\n" +
            "                processData(data);\n" +
            "            })\n" +
            "            .catch(catchRequestError)\n" +
            "        }\n" +
            "\n" +
            "        getData();\n" +
            "    </script>\n" +
            "</html>\n";
}
