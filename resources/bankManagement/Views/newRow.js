export const addNewRow = () => {
    const whichTableMainSecton = document.getElementById('which-table-main-secton');
    const mainSection = document.getElementById('main-section');
    whichTableMainSecton.classList.add("blurBackground");
    const addNewRowDiv = document.getElementById("add-new-row-div");
    mainSection.classList.add("blurBackground");
    addNewRowDiv.classList.remove("add-new-row-div");
    addNewRowDiv.classList.add("add-new-row-div-visible");
}

export const addNewRowInside = async (headers) => {
    let url;
    // get context on which table it is
    const table = document.getElementById("which-table").value;
    if (table === 'user-accounts') {
        url = 'http://localhost:7000/api/createNewAccount';
    } else if (table === 'account-type') {
        url = 'http://localhost:7000/api/createNewAccountType';
    }
    let json = {};
    headers.forEach((h) => {
        if (h === 'balance' ||
            h === 'account_creation_timestamp' ||
            h === 'account_updation_timestamp' ||
            h === 'account_created_by' ||
            h === 'id' ||
            h === 'account_type_timestamp') {
            return;
        }
        json[h] = document.getElementById(h + "-add").value === "" ? "null" : document.getElementById(h + "-add").value;
    })
    for (const [key, value] of Object.entries(json)) {
        if (value === "null") {
            alert(key + " is null");
            location.reload();
            return;
        }
        
        if (value.includes(",")) {
            alert(key + " cannot contain strings with , value ");
            location.reload();
            return;
        }
    } 
    let responseJson = {};
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        });
        responseJson = await response.json();
        if ('error' in responseJson) {
            alert(responseJson['error']);
        } else {
            alert(responseJson['success']);
        }
    } catch (e) {
        alert("Fetching error")
    }
    
    location.reload();
    return responseJson;
}

export const closeButton = () => {
    const addNewRowDiv = document.getElementById("add-new-row-div");
    const mainSection = document.getElementById('main-section');
    const whichTableMainSecton = document.getElementById('which-table-main-secton');
    mainSection.classList.remove("blurBackground");
    whichTableMainSecton.classList.remove("blurBackground");
    addNewRowDiv.classList.remove("add-new-row-div-visible");
    addNewRowDiv.classList.add("add-new-row-div");
}