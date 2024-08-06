export const editConfigDetails = (headers) => {
    document.getElementById('config-details').innerHTML = `
        <h5 class="edit-config-details-header">Edit config details (Enter the ID and value of field or fields
            you want to edit)</h5>
        ${headers.map((head) => {
            if (head === 'balance' || head === 'account_created_by' || head === 'account_creation_timestamp' || head === 'account_updation_timestamp' || head === 'account_type_timestamp') {
                return null;
            }
            return `
            <h5>${head}</h5>
            <input class="search-box" id="${head}-update"></input>
            `;
        }).join('')} 
        <br />
        <br />
        <button id="update-edit-config">Change field</button>
    `
}

export const removeEditConfigDetails = () => {
    document.getElementById('config-details').innerHTML = ``;
}

export const updateRecord = async (headers) => {
    let url;
    let json = {};
    for (let i = 0; i < headers.length; i++) {
        const head = headers[i];
        if (head === 'balance' || head === 'account_created_by' || head === 'account_creation_timestamp' || head === 'account_updation_timestamp') {
            continue;
        }
        if (!document.getElementById(head + "-update")) continue;

        json[head] = document.getElementById(head + "-update").value; 
        if (json['account_number'] === '' || json['id'] === '') {
            alert("Please enter the id field for edit config details");
            location.reload();
            return;
        }
        // delete key if empty string as that is not to be updated
        for (const [key] of Object.entries(json)) {
        if (json[key].includes(",")) {
                alert(key + " cannot contain strings with , value ");
                location.reload();
                return;
            }
            if (json[key] === '') {
                delete json[key];
            }
        }
    } 
    if (headers[0] === "account_number") {
        // this is user accounts
        url = "http://localhost:7000/api/updateAccountFields";

    } else if (headers[0] === "id") {
        // this is account type
        url = "http://localhost:7000/api/updateAccountType";
    } 
    try {
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        });
        const resJson = await response.json();
        if ("error" in resJson) {
            alert(resJson['error']);
            location.reload();
        } else if ("failure" in resJson) {
            alert(resJson['failure']);
            location.reload();
        } else if ("success" in resJson) {
            alert(resJson['success']);
            location.reload();
        }
    } catch (e) {
        console.error(e);
        alert(e);
    }
}