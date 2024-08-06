import { editConfigDetails, updateRecord, removeEditConfigDetails } from "./editConfigDetails.js";
import { filterSearchOptions } from "./filterSearchOptions.js";
import { disableStuffForTransactions, enableStuff } from "./transactionStuff.js";
import { getHeadersForTable } from "./getHeadersForTable.js";
import { renderDepositAndWithdraw, reverseDepositAndWithdraw,addDepositToDatabase, withdrawFromDatabase } from "./depositAndWithDraw.js";
import { addNewRowHeaders } from "./addNewRowHeaders.js";
import { getLatestTransaction, revertLatestTransaction } from "./latestTransaction.js";
import { closeButton } from "./addNewRowHeaders.js";
import { fetchLatestTransaction } from "./latestTransaction.js";
import { deleteRow, deleteRowFromDB, deleteRowRemove } from './deleteRow.js';
import { fetchBalanceOfAnAccount, revertBalanceOfAnAccount, renderBalanceOfAnAccount } from './balanceOfAAccount.js';
import { download, fetchFile } from "./jsonToCsv.js";
import { dateStartFunctionality, dateEndFunctionality } from './Dates.js';
import { addEmail } from "./addEmail.js";
import { addNewRow, addNewRowInside } from "./newRow.js";

let json = {};
let headers = [];

export const likeSearch = async (url, headers) => {
    let searchField = document.getElementById('search-fields').value;
    let searchFieldValue = document.getElementById('search-field-value').value;
    // include fields to also have dates
    let dateStart = document.getElementById('date-start').value;
    let dateEnd = document.getElementById('date-end').value;
    let requestJson = {
        field_name: searchField,
        field_value: searchFieldValue
    };
    if (dateStart !== '' && dateEnd !== '') {
        requestJson['start_date'] = dateStart + " 00:00:00";
        requestJson['end_date'] = dateEnd + " 00:00:00";
    }
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestJson)
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        json = await response.json();
        if ("data" in json) {
            renderTableData(json, headers);
        } else {
            console.error("Unexpected response format:", json);
        }
    } catch (e) {
        console.error(e);
    }
}


export const searchDriver = async () => {
    const value = document.getElementById("which-table").value;
    if (value === 'transactions') {
        headers = ["txn_id", "account_number", "transaction_type", "amount", "txn_timestamp"]
        await likeSearch('http://localhost:7000/api/searchTransaction', headers);
    } else if (value === 'user-accounts') {
        headers = ["account_number", "first_name", "last_name", "username", "phone_number", "nominee_first_name", "nominee_last_name", "account_type", "balance", "account_created_by", "account_creation_timestamp", "account_updation_timestamp"]
        await likeSearch('http://localhost:7000/api/searchAccount', headers);
    } else if (value === 'account-type') {
        headers = ["id", "account_type", "account_type_timestamp"]
        await likeSearch('http://localhost:7000/api/searchAccountType', headers);
    }
}


export const renderTableData = (json, tableHeaders) => {
    document.querySelector('table').innerHTML = `
        <thead>
            <tr>
                ${tableHeaders.map((header) => `<th>${header}</th>`).join("")}
            </tr>
        </thead>
        <tbody>
            ${json['data'].map((row) => ` 
                <tr>
                    ${tableHeaders.map((column) => `<td>${row[column]}</td>`).join("")}
                </tr>
            `)}
        </tbody>
    `
    
}

const getWhichTable = async () => {
    const value = document.getElementById("which-table").value;
    if (value.toLowerCase() === "transactions") {
        document.getElementById('search-field-value').value = '';
        document.getElementById('date-start').value = '';
        document.getElementById('date-end').value = '';
        headers = ["txn_id", "account_number", "transaction_type", "amount", "txn_timestamp"];
        const response = await fetch("http://localhost:7000/api/getEntireTransactionHistory");
        json = await response.json();
        renderTableData(json, headers);
        editConfigDetails(headers);
        filterSearchOptions(headers);
        addNewRowHeaders(headers);
        disableStuffForTransactions();
        removeEditConfigDetails();
        reverseDepositAndWithdraw();
        getLatestTransaction();
        deleteRowRemove();
        revertBalanceOfAnAccount();
    } else if (value.toLowerCase() === "user-accounts") {
        document.getElementById('search-field-value').value = '';
        document.getElementById('date-start').value = '';
        document.getElementById('date-end').value = '';
        headers = ["account_number", "first_name", "last_name", "username", "phone_number", "nominee_first_name", "nominee_last_name", "account_type", "balance", "account_created_by", "account_creation_timestamp", "account_updation_timestamp"]
        const response = await fetch("http://localhost:7000/api/getAllAccounts");
        json = await response.json();
        renderTableData(json, headers);
        editConfigDetails(headers);
        filterSearchOptions(headers);
        addNewRowHeaders(headers);
        renderDepositAndWithdraw();
        revertLatestTransaction();
        enableStuff();
        deleteRow("account_number");
        renderBalanceOfAnAccount();
    } else if (value.toLowerCase() === "account-type") {
        document.getElementById('search-field-value').value = '';
        document.getElementById('date-start').value = '';
        document.getElementById('date-end').value = '';
        headers = ["id", "account_type", "account_type_timestamp"]
        const response = await fetch("http://localhost:7000/api/getAllAccountTypes");
        json = await response.json();
        renderTableData(json, headers);
        editConfigDetails(headers);
        filterSearchOptions(headers);
        addNewRowHeaders(headers);
        reverseDepositAndWithdraw();
        revertLatestTransaction();
        enableStuff();
        deleteRow("account_type");
        revertBalanceOfAnAccount();
    }
}

(async () => {
    try {
        const response = await fetch("http://localhost:7000/api/getAllAccountTypes");
        json = await response.json();
        const headers = ["id", "account_type", "account_type_timestamp"]
        renderTableData(json, headers);
        getWhichTable();
    } catch (e) {
        console.error(e);
    }
})();

document.getElementById("which-table").addEventListener("change", async () => {
    await getWhichTable();
});

document.getElementById('search-field-value').addEventListener('change', async () => {
    await searchDriver();
});


document.getElementById("add-new-row-button").addEventListener("click", () => {
    addNewRow();
});

console.log("add-new-row-bt is " + document.getElementById("add-new-row-bt"));

document.addEventListener("click", async (e) => {
    if (e.target.id === 'add-new-row-bt') {
        const headers = await getHeadersForTable();
        await addNewRowInside(headers);
    }
});

document.addEventListener("click", async (event) => {
    if (event.target.id === "bt-deposit") await addDepositToDatabase();
});

document.addEventListener("click", async (event) => {
    if (event.target.id === "bt-withdraw") await withdrawFromDatabase();
});

document.addEventListener("click", async (event) => {
    if (event.target.id === "latest-transaction-bt") await fetchLatestTransaction();
});

document.addEventListener("click", (event) => {
    if (event.target.id === "close-button") closeButton();
});

document.addEventListener("click", async (e) => {
    if (e.target.id === "update-edit-config") {
        const headers = await getHeadersForTable();
        await updateRecord(headers);
    }
});

document.addEventListener("click", async (event) => {
    if (event.target.id === "account_number-del") {
        await deleteRowFromDB("account_number");
    }
});

document.addEventListener("click", async (event) => {
    if (event.target.id === "account_type-del") {
        await deleteRowFromDB("account_type");
    }
});

document.addEventListener("click", async (event) => {
    if (event.target.id === "account-number-balance-button") {
        await fetchBalanceOfAnAccount();
    }
});

document.getElementById("date-start").addEventListener("change", async () => {
    await dateStartFunctionality(searchDriver);
});

document.getElementById("date-end").addEventListener("change", async () => {
    await dateEndFunctionality(searchDriver);
});

document.getElementById('downloadButton').addEventListener('click', async () => {
    // e.preventDefault();
    await fetchFile(json);
});

document.addEventListener("click", async (event) => {
    if (event.target.id === "emailButton") {
        await addEmail(json);
    }
});