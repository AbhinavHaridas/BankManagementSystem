export const dateStartFunctionality = async (searchDriver) => {
    let today = new Date();
    today.setDate(today.getDate() + 1);
    const day = today.getDate();
    const month = today.getMonth() + 1;
    const year = today.getFullYear();

    const dateEndValue = document.getElementById("date-end").value;
    const dateStartValue = document.getElementById("date-start").value;

    const details = dateStartValue.split("-");
    const dayInputted = parseInt(details[2]);
    const monthInputted = parseInt(details[1]);
    const yearInputted = parseInt(details[0]);

    // Create date objects for comparison
    const inputDate = new Date(yearInputted, monthInputted - 1, dayInputted);
    const currentDate = new Date(year, month - 1, day);

    // Case when user clears the start date but end date still remains
    if (dateStartValue === '') {
        alert("End date cannot be without start date");
        location.reload();
        return;
    }

    if (inputDate > currentDate) {
        alert("Sorry, the start date cannot be ahead of the current date");
        location.reload();
        return;
    }

    // Fill date end if date start is already inserted
    if (dateStartValue !== '') {
        document.getElementById('date-end').value = `${today.getFullYear()}-${(today.getMonth() + 1).toString().padStart(2, '0')}-${today.getDate().toString().padStart(2, '0')}`;
    }

    // Validate if date start is not ahead of date end
    if (dateEndValue !== '') {
        const endDateInputtedDetails = dateEndValue.split("-");
        const yearEndDateInputted = parseInt(endDateInputtedDetails[0], 10);
        const monthEndDateInputted = parseInt(endDateInputtedDetails[1], 10);
        const dayEndDateInputted = parseInt(endDateInputtedDetails[2], 10);

        const endDate = new Date(yearEndDateInputted, monthEndDateInputted - 1, dayEndDateInputted);

        if (inputDate > endDate) {
            alert("Sorry, the end date can't be behind the start date");
            location.reload();
            return;
        }
    }

    await searchDriver();
}


export const dateEndFunctionality = async (searchDriver) => {
    const today = new Date();
    today.setDate(today.getDate() + 1);
    const day = today.getDate();
    const month = today.getMonth() + 1;
    const year = today.getFullYear();
    const details = document.getElementById("date-end").value.split("-");
    const dayInputted = parseInt(details[2]);
    const monthInputted = parseInt(details[1]);
    const yearInputted = parseInt(details[0]);

    // case when user clears the end date but start date still remains
    if (document.getElementById("date-start").value !== '' && document.getElementById("date-end").value === '') {
        document.getElementById('date-end').value = year + "-" + (month).toString().padStart(2, '0') + "-" + parseInt(day).toString().padStart(2, '0');
    }

    const inputDate = new Date(yearInputted, monthInputted - 1, dayInputted);
    const currentDate = new Date(year, month - 1, day);

    if (inputDate > currentDate) {
        alert("Sorry the start date cannot be ahead of current date");
        location.reload();
        return;
    }

    // validate if date start is not ahead of date end
    if (document.getElementById('date-start').value !== '') {
        const startDateInputted = document.getElementById('date-start').value;
        const startDateInputtedDetails = startDateInputted.split("-");
        const yearStartDateInputted = parseInt(startDateInputtedDetails[0]);
        const monthStartDateInputted = parseInt(startDateInputtedDetails[1]);
        const dayStartDateInputted = parseInt(startDateInputtedDetails[2]);

        const startDate = new Date(yearStartDateInputted, monthStartDateInputted - 1, dayStartDateInputted);
        if (inputDate < startDate) {
            alert("Sorry the end date cant be behind the start date");
            location.reload();
            return;
        }
    } else {
        alert("Please specify a start date");
        location.reload();
        return;
    }

    await searchDriver();

}