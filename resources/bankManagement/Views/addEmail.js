export const addEmail = async (json) => {
    // get attachment type
    console.log(JSON.stringify(json));
    let attachmentType = document.getElementById('attachment-types-download').value;
    json['details']['attachment_type'] = attachmentType;
    try {
        const response = await fetch("http://localhost:7000/api/sendMail", {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(json) 
        });

        const responseJson = await response.json();
        console.log(responseJson);
        if ("error" in responseJson) {
            alert(responseJson["error"]);
            return;
        } 
        alert(responseJson["success"]);
    } catch (e) {
        alert(e);
    }
}