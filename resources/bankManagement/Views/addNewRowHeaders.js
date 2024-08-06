export const closeButton = () => {
    const addNewRowDiv = document.getElementById("add-new-row-div");
    const mainSection = document.getElementById('main-section');
    const whichTableMainSecton = document.getElementById('which-table-main-secton');
    mainSection.classList.remove("blurBackground");
    whichTableMainSecton.classList.remove("blurBackground");
    addNewRowDiv.classList.remove("add-new-row-div-visible");
    addNewRowDiv.classList.add("add-new-row-div");
}

export const addNewRowHeaders = (headers) => {
    const addNewRowDiv = document.getElementById("add-new-row-div");
    addNewRowDiv.innerHTML = `
        <div><h5 id="close-button">Close</h5></div>
        <h5 class="add-new-row-header">Add new row</h5>
        ${headers.map((h) => {
        if (h === 'balance' ||
            h === 'account_creation_timestamp' ||
            h === 'account_updation_timestamp' ||
            h === 'account_created_by' ||
            h === 'id' ||
            h === 'account_type_timestamp') {
            return null;
        }
        return `<h5>${h}</h5>
        <input type="text" class="search-box" name="${h}" id="${h}-add"></input>`;
    }).join("")}
        <br />
        <br />
        <button id="add-new-row-bt">Add new row</button>`;
}