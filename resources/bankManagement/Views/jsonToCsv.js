// export const jsonToCsv = (json, headers) => {
//     console.log(JSON.stringify(json));
//     console.log("went to jsontocsv function");
//     let csvRows = [];
//     // insert table name
//     csvRows.push('table,' + json['details']['table'])
//     if (('field_name' in json['details'] && json['details']['field_name'] !== '')
//         && ('field_value' in json['details'] && json['details']['field_value'] !== '')) {
//         // field name and field value is being entered
//         csvRows.push('field_name,' + json['details']['field_name']);
//         csvRows.push('field_value_pattern,' + json['details']['field_value']);
//     }
//     if ('start_date' in json['details']
//         && 'end_date' in json['details']) {
//         // start_date and end_date is being entered
//         csvRows.push('start_date,' + json['details']['start_date'].split(" ")[0]);
//         csvRows.push('end_date,' + json['details']['end_date'].split(" ")[0]);
//     }
//     csvRows.push('\n');
//     csvRows.push(headers.join(','));
//     json['data'].forEach((record) => {
//         csvRows.push(Object.values(record).toString());
//     });
//     let fileName = json['details']['table'];
//     if (('start_date' in json['details']) && ('end_date' in json['details'])) {
//         let startDateWithoutTime = json['details']['start_date'].split(" ")[0];
//         let endDateWithoutTime = json['details']['end_date'].split(" ")[0];
//         fileName += " " + startDateWithoutTime + " " + endDateWithoutTime;
//     }
//     return [csvRows.join('\n'), fileName];
// }

export const fetchFile = async (json) => {
    const downloadButton = document.getElementById('downloadButton');
    downloadButton.disabled = true;
    json['details']['attachment_type'] = document.getElementById('attachment-types-download').value;
    console.log(document.getElementById('attachment-types-download').value);
    try {
        const response = await fetch('http://localhost:7000/api/download', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json) 
        });

        const blob = await response.blob();
        console.log(response.headers ? response.headers : "nothing here");
        const fileName = response.headers.get('Content-Disposition').split("filename=")[1];
        const fileType = response.headers.get('Content-Type');
        download(downloadButton, blob, fileName, fileType);
    } catch (e) {
        alert(e);
    }
}

export const download = (downloadButton, blob, fileName, fileType) => {
    const url = URL.createObjectURL(blob, { type: fileType });
    const tempLink = document.createElement('a');
    tempLink.href = url;
    tempLink.download = fileName;
    tempLink.style.display = 'none';
    tempLink.innerText = 'Download';

    document.getElementById('downloadButton').appendChild = tempLink;
    tempLink.click(); 
    document.getElementById('downloadButton').removeChild = tempLink;

    setTimeout(() => {
        URL.revokeObjectURL(url);
        downloadButton.disabled = false;
    }, 100);
}