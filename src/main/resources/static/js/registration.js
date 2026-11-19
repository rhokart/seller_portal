let currentSection = 0;
const sections = document.querySelectorAll('.form-section');
const progress = document.getElementById('progress');
const nextBtn = document.getElementById('nextBtn');
const prevBtn = document.getElementById('prevBtn');
const submitBtn = document.getElementById('submitBtn');

// Function to show loader
function showLoader() {
    document.getElementById('loaderContainer').style.display = 'flex';
}

// Function to hide loader
function hideLoader() {
     document.getElementById('loaderContainer').style.display = 'none';
}
// Show current section
function showSection(index) {
    sections.forEach((section, i) => {
        section.classList.toggle('active', i === index);
    });
    progress.style.width = `${((index + 1) / sections.length) * 100}%`;
    prevBtn.style.display = index === 0 ? 'none' : 'block';
    nextBtn.style.display = index === sections.length - 1 ? 'none' : 'block';
    submitBtn.style.display = index === sections.length - 1 ? 'block' : 'none';
}

//  GST checking and PAN extractions.
function check_gst(){
        const gst = document.getElementById('gstin').value;
        let errorFlag = false;
        let pan = '';

        if(gst.length != 15){
            gstError.textContent = 'Invalid Length of GSTIN.';
            errorFlag = true;
        }else{
            var state = parseInt(gst.substring(0, 2));
            // FIRST 2 CHARACTERS STATE CODE
            if(state < 1 || state > 37 || isNaN(state)){
                console.log(state);
                console.log('state validation failed');
                gstError.textContent = 'Invalid First Two Characters of GSTIN.';
                errorFlag = true;
            }
            // NEXT 10 CHARACTERS PAN NO. VALIDATION
            pan = gst.substring(2, 12).toUpperCase();
            var regex = /[a-zA-Z]{3}[PCHFATBLJG]{1}[a-zA-Z]{1}[0-9]{4}[a-zA-Z]{1}$/;
            if( !regex.test(pan) ){
                gstError.textContent = 'Invalid PAN of GSTIN.';
                errorFlag = true;
            }
            // DEFAULT 14TH CHARACTER 'Z'
            var char14 = gst[13].toUpperCase();
            if(char14 != "Z"){
               gstError.textContent = "14th character of GSTIN should be 'Z'";
               errorFlag = true;
            }
        }
        if(errorFlag == false){
            gstError.textContent = '';
            document.getElementById('pan').value = pan;
            console.log(pan);
        }
    }
    function extractPAN_Number(str)
    {
       const strPattern = [ "[A-Z]{5}[0-9]{4}[A-Z]{1}" ];
        for (let i = 0; i < 1; i++) {
            const pattern = new RegExp(strPattern[i]);
            const words_begin = str.matchAll(pattern);
            console.log("The PAN Number that above string contains:");
            for (const match of words_begin) {
                console.log(match[0]);
            }
        }
    }

// Next button click
nextBtn.addEventListener('click', () => {

    if (currentSection < sections.length - 1) {
        currentSection++;
        showSection(currentSection);
    }
});

// Previous button click
prevBtn.addEventListener('click', () => {
    if (currentSection > 0) {
        currentSection--;
        showSection(currentSection);
    }
});

// OTP functionality with API calls
function setupOtpFunctionality(otpSendBtnId, otpInputId, verifyBtnId, statusId, emailFieldId) {
    const otpSendBtn = document.getElementById(otpSendBtnId);
    const otpInput = document.getElementById(otpInputId);
    const verifyBtn = document.getElementById(verifyBtnId);
    const status = document.getElementById(statusId);
    const emailField = document.getElementById(emailFieldId);

    otpSendBtn.addEventListener('click', () => {
        if (!emailField.value) {
            alert("Please enter your email before requesting OTP.");
            return;
        }

        showLoader();
        // Send OTP request to the backend API
        fetch("/pages/getOtpAndValidate?email=" + encodeURIComponent(emailField.value))
            .then(response => response.text())
            .then(data => {
                if (data === "OTP Sent") {
                    otpInput.style.display = 'inline-block';
                    verifyBtn.style.display = 'inline-block';
                    verifyBtn.disabled = true;
                    verifyBtn.classList.add('disabled');
                    status.innerHTML = "OTP Sent. Please check your email.";
                } else {
                    status.innerHTML = "Failed to send OTP.";
                }
                hideLoader();
            })
            .catch(error => {
                console.error("Error sending OTP:", error);
                status.innerHTML = "Failed to send OTP.";
                hideLoader();
            });
    });

    otpInput.addEventListener('input', () => {
        if (otpInput.value.length === 5) {
            verifyBtn.disabled = false;
            verifyBtn.classList.remove('disabled');
        } else {
            verifyBtn.disabled = true;
            verifyBtn.classList.add('disabled');
        }
    });

    verifyBtn.addEventListener('click', () => {
        if (otpInput.value.length === 5) {
            // Call API for OTP verification
            fetch("/pages/getOtpAndValidate?email=" + encodeURIComponent(emailField.value) + "&otpValue=" + encodeURIComponent(otpInput.value))
                .then(response => response.text())
                .then(data => {
                    if (data === "OTP Verified Successfully") {
                        status.innerHTML = "✓ Verified";
                        otpSendBtn.style.display = "none";
                        otpInput.style.display = "none";
                        verifyBtn.style.display = "none";
                    } else {
                        status.innerHTML = "❌ OTP did not match. Try again.";
                    }
                })
                .catch(error => {
                    console.error("Error verifying OTP:", error);
                    status.innerHTML = "OTP verification failed.";
                });
        }
    });
}

// Setup OTP functionality for all fields
setupOtpFunctionality('mobOtpSendBtn', 'mobOtp', 'mobOtpvalidationBtn', 'mobVerificationStatus', 'phoneNo');
setupOtpFunctionality('emailOtpSendBtn', 'emailOtp', 'emailOtpvalidationBtn', 'emailVerificationStatus', 'email');
setupOtpFunctionality('altEmailOtpSendBtn', 'altEmailOtp', 'altEmailOtpvalidationBtn', 'altEmailVerificationStatus', 'altEmail');
setupOtpFunctionality('altMobOtpSendBtn', 'altMobOtp', 'altMobOtpvalidationBtn', 'altMobVerificationStatus', 'altphoneNo');



// Auto-fill billing address
document.getElementById('sameAsBusiness').addEventListener('change', (e) => {
    if (e.target.checked) {
        document.getElementById('billPincode').value = document.getElementById('pincode').value;
        document.getElementById('billCity').value = document.getElementById('city').value;
        document.getElementById('billState').value = document.getElementById('state').value;
        document.getElementById('billCountry').value = document.getElementById('country').value;
    } else {
        document.getElementById('billPincode').value = '';
        document.getElementById('billCity').value = '';
        document.getElementById('billState').value = '';
        document.getElementById('billCountry').value = '';
    }
});


document.getElementById('sameAsBusinessPickup').addEventListener('change', (e) => {
    const fields = ["pickPincode", "pickCity", "pickState", "pickCountry"];
    const sourceFields = ["pincode", "city", "state", "country"];

    if (e.target.checked) {
        fields.forEach((field, index) => {
            document.getElementById(field).value = document.getElementById(sourceFields[index]).value;
            document.getElementById(field).dispatchEvent(new Event("input")); // ✅ Trigger validation
        });
    } else {
        fields.forEach(field => {
            document.getElementById(field).value = '';
            document.getElementById(field).dispatchEvent(new Event("input")); // ✅ Trigger validation
        });
    }
});

    document.addEventListener("DOMContentLoaded", function () {
        const termsCheckbox = document.getElementById("acceptPrivacyPolicy");
        const mailingCheckbox = document.getElementById("receiveInfo");
        const submitButton = document.getElementById("submitBtn");

        function updateButtonState() {
                    submitBtn.disabled = !(termsCheckbox.checked && mailingCheckbox.checked);
                }
        updateButtonState();
        termsCheckbox.addEventListener("change", updateButtonState);
        mailingCheckbox.addEventListener("change", updateButtonState);
    });




document.addEventListener("DOMContentLoaded", function () {
    const sections = document.querySelectorAll(".form-section");
    const nextBtn = document.getElementById("nextBtn");
    const prevBtn = document.getElementById("prevBtn");
    const accountField = document.getElementById("accNo");
    const confirmField = document.getElementById("cnfaccNo");
    const emailField = document.getElementById("email");
    const altEmailField = document.getElementById("altEmail");
    const form = document.getElementById("registrationForm");
    const errorSpan = document.getElementById("accountError");
    let currentSectionIndex = 0;

    // Account number validation
    function validateAccountNumbers() {
        const account = accountField.value.trim();
        const confirmAccount = confirmField.value.trim();

        if (account !== "" && confirmAccount !== "" && account !== confirmAccount) {
            errorSpan.innerText = "Account numbers do not match!";
            confirmField.style.borderColor = "red";
            return false;
        } else {
            errorSpan.innerText = "";
            confirmField.style.borderColor = "";
            return true;
        }
    }

    // Email field validation
    function validateEmailFields() {
        const email = emailField.value.trim();
        const altEmail = altEmailField.value.trim();
        const errorSpan = document.getElementById("altEmailError");
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!errorSpan) {
            const span = document.createElement("span");
            span.id = "altEmailError";
            span.className = "error";
            altEmailField.parentNode.appendChild(span);
        }

        if(!emailPattern.test(email)){
            document.getElementById("altEmailError").innerText = "Type correct email address.";
            document.getElementById("altEmailError").style.color = "red";
            altEmailField.style.borderColor = "red";
            return false;
        }
        if(!emailPattern.test(altEmail) && altEmail !== ""){
           document.getElementById("altEmailError").innerText = "Type correct alternate email address.";
           document.getElementById("altEmailError").style.color = "red";
           altEmailField.style.borderColor = "red";
           return false;
         }

        if (email === altEmail && altEmail !== "") {
            document.getElementById("altEmailError").innerText = "Alternate email cannot be the same as primary email.";
            document.getElementById("altEmailError").style.color = "red";
            altEmailField.style.borderColor = "red";
            return false;
        }

        document.getElementById("altEmailError").innerText = "";
        altEmailField.style.borderColor = "";
        return true;

    }

    function validateSection(section) {
        const requiredFields = section.querySelectorAll("input[required], select[required], textarea[required]");
        let isValid = true;

        requiredFields.forEach(field => {
            if (field.type === "file") {
                // Check if a file is uploaded
                if (field.files.length === 0) {
                    isValid = false;
                    field.style.borderColor = "red";
                    field.parentElement.querySelector('.file-label').style.borderColor = "red";
                } else {
                    field.style.borderColor = "";
                    field.parentElement.querySelector('.file-label').style.borderColor = "";
                }
            } else {
                if (!field.value.trim()) {
                    isValid = false;
                    field.style.borderColor = "red";
                } else {
                    field.style.borderColor = "";
                }
            }
        });

        return isValid;
    }

    // Update the "Next" button state
    function updateNextButtonState() {
        const currentSection = sections[currentSectionIndex];
        let isValidSection = validateSection(currentSection);

        // Validate account numbers if present in the section
        if (currentSection.contains(accountField) && currentSection.contains(confirmField)) {
            isValidSection = isValidSection && validateAccountNumbers();
        }

        // Validate email fields if present in the section
        if (currentSection.contains(emailField) && currentSection.contains(altEmailField)) {
            isValidSection = isValidSection && validateEmailFields();
        }

        nextBtn.disabled = !isValidSection;
    }

    // Show the current section based on the index
    function showSection(index) {
        sections.forEach((section, i) => {
            section.style.display = i === index ? "block" : "none";
        });
        updateNextButtonState();
    }

    // Attach event listeners to required fields
    document.querySelectorAll("input[required], select[required]").forEach(input => {
        if (input.type === "file") {
            // For file inputs, listen to both change and input events
            input.addEventListener("change", () => {
                updateNextButtonState();
                // Update file name display
                const fileName = input.files.length > 0 ? input.files[0].name : "";
                const fileNameSpan = input.parentElement.querySelector('.file-name');
                if (fileNameSpan) {
                    fileNameSpan.textContent = fileName;
                }
            });
        }
        input.addEventListener("input", updateNextButtonState);
    });

    // Add a specific event listener for the documentation section
    const documentationSection = document.getElementById("documentation");
    if (documentationSection) {
        const fileInputs = documentationSection.querySelectorAll('input[type="file"]');
        fileInputs.forEach(input => {
            input.addEventListener("change", () => {
                setTimeout(updateNextButtonState, 0); // Ensure validation runs after file is processed
            });
        });
    }

    // Attach event listener for account fields
    accountField.addEventListener("input", updateNextButtonState);
    confirmField.addEventListener("input", updateNextButtonState);

    // Attach event listener for email fields
    altEmailField.addEventListener("input", updateNextButtonState);

    altEmailField.addEventListener("paste", function (e) {
        e.preventDefault();
        alert("Copy-paste is not allowed. Please type manually.");
    });

    // Handle "Next" button click
    nextBtn.addEventListener("click", function () {
        if (currentSectionIndex < sections.length - 1) {
            currentSectionIndex++;
            showSection(currentSectionIndex);
        }
    });

    // Handle "Previous" button click
    prevBtn.addEventListener("click", function () {
        if (currentSectionIndex > 0) {
            currentSectionIndex--;
            showSection(currentSectionIndex);
        }
    });

    // Show the initial section
    showSection(currentSectionIndex);
});



document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registrationForm');

    form.addEventListener('submit', function(event) {
        event.preventDefault();
        submitForm();
    });

    function submitForm() {
        const formData = new FormData(form);
                const brandDetails = [];

                document.querySelectorAll('.brand-group').forEach((group, index) => {
                    const brandSelect = group.querySelector('.brandSelect');
                    const associationSelect = group.querySelector('.associationSelect');
                    const fileInput = group.querySelector('.file-upload-brand input');

                    brandDetails.push({
                        brandName: brandSelect.value,
                        associationType: associationSelect.value,
                        certificate: fileInput && fileInput.files.length > 0 ? fileInput.files[0].name : null
                    });
                    // Append each brand certificate file (if available)
                    if (fileInput && fileInput.files.length > 0) {
                        for (let i = 0; i < fileInput.files.length; i++) {
                            formData.append("brandCertificate", fileInput.files[i]); // Correct way to append multiple files
                        }
                    }
                });
            // Manually check if the checkboxes are checked
            const isTncChecked = document.getElementById('acceptPrivacyPolicy').checked;
            const isPermissionChecked = document.getElementById('receiveInfo').checked;
                    // Manually add auto-filled billing address fields to FormData
                    if (document.getElementById('sameAsBusiness').checked) {
                        formData.set('billPincode', document.getElementById('billPincode').value);
                        formData.set('billCity', document.getElementById('billCity').value);
                        formData.set('billState', document.getElementById('billState').value);
                        formData.set('billCountry', document.getElementById('billCountry').value);
                    }

                    // Manually add auto-filled pickup address fields to FormData
                    if (document.getElementById('sameAsBusinessPickup').checked) {
                        formData.set('pickPincode', document.getElementById('pickPincode').value);
                        formData.set('pickCity', document.getElementById('pickCity').value);
                        formData.set('pickState', document.getElementById('pickState').value);
                        formData.set('pickCountry', document.getElementById('pickCountry').value);
                    }


        const sellerInfo = {
            businessDetail: {
                gstin: formData.get('gstin'),
                entityName: formData.get('entityName'),
                contactName: formData.get('contactName'),
                tradeName: formData.get('tradeName'),
                country: formData.get('country'),
                pincode: formData.get('pincode'),
                state: formData.get('state'),
                city: formData.get('city'),
                tan: formData.get('tan'),
                pan: formData.get('pan'),
                phoneNo: formData.get('phoneNo'),
                email: formData.get('email'),
                altEmail: formData.get('altEmail'),
                altPhoneNo: formData.get('altPhoneNo')
            },
            bankDetail: {
                accName: formData.get('accName'),
                accNo: formData.get('accNo'),
                ifsc: formData.get('ifsc'),
                bankName: formData.get('bankName'),
                branchName: formData.get('branchName'),
                branchCityName: formData.get('branchCityName'),
                accountType: formData.get('accountType')
            },
            documents: {
                // You'll need to handle file uploads separately
                // This is just a placeholder
                ownerPan: formData.get('uploadOwnerPan') ? formData.get('uploadOwnerPan').name : null,
                gstin: formData.get('uploadGstin') ? formData.get('uploadGstin').name : null,
                incCertificate: formData.get('incCertificate') ? formData.get('incCertificate').name : null,
                businessAddressProof: formData.get('businessAddressProof') ? formData.get('businessAddressProof').name : null,
                signature: formData.get('signature') ? formData.get('signature').name : null
            },
            sellerAddress: {
                billingAddress: {
                    addressLine1: document.getElementById('addressLine1').value,
                    addressLine2: document.getElementById('addressLine2').value,
                    landmark: document.getElementById('landmark').value,
                    pincode: document.getElementById('billPincode').value,
                    city: document.getElementById('billCity').value,
                    district: document.getElementById('billDistrict').value,
                    state: document.getElementById('billState').value,
                    country: document.getElementById('billCountry').value
                },
                pickupAddress: {
                    addressLine1: document.getElementById('pickAddressLine1').value,
                    addressLine2: document.getElementById('pickAddressLine2').value,
                    picklandmark: document.getElementById('picklandmark').value,
                    pincode: document.getElementById('pickPincode').value,
                    city: document.getElementById('pickCity').value,
                    district: document.getElementById('pickDistrict').value,
                    state: document.getElementById('pickState').value,
                    country: document.getElementById('pickCountry').value
                }
            },
            brands: brandDetails,
            isTnc: isTncChecked,
            isPermission: isPermissionChecked
        };
        // Append JSON as a Blob to FormData
        formData.append("sellerInfo", new Blob([JSON.stringify(sellerInfo)], { type: "application/json" }));

        // Append files separately (if they exist)
        if (formData.get('uploadOwnerPan')) formData.append('uploadOwnerPan', formData.get('uploadOwnerPan'));
        if (formData.get('uploadGstin')) formData.append('uploadGstin', formData.get('uploadGstin'));
        if (formData.get('incCertificate')) formData.append('incCertificate', formData.get('incCertificate'));
        if (formData.get('businessAddressProof')) formData.append('businessAddressProof', formData.get('businessAddressProof'));
        if (formData.get('signature')) formData.append('signature', formData.get('signature'));
        // Show loader
        showLoader();

        console.log()
        fetch('/api/v1/seller/register', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data.refId);
            //alert('Seller registered successfully');
            showSuccessMessage(data.refId);
            // You might want to redirect the user or clear the form here
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Error registering seller');
        })
        .finally(() => {
            // Hide loader
            hideLoader();
        });
    }
});

function showSuccessMessage(referenceId) {
    const successDiv = document.getElementById('successMessage');

    successDiv.innerHTML = `
        <p style="color: green; font-weight: bold;">Thank You! You are done with the onboarding process.</p>
        <p style="color: green;"><strong>Your reference ID is - ${referenceId}</strong></p>
        <p>Your submission is under approval. You will receive a confirmation regarding the next steps on your email ID and mobile number.</p>
        <p>You may also reach out to <a href="mailto:seller_support@rhokart.com">seller_support@rhokart.com</a> for any clarification with your reference ID screenshot and registered phone number.</p>
    `;
    // Show the success message div
    successDiv.style.display = 'block';
    // Hide the form after submission
    document.getElementById('registrationForm').style.display = 'none';
    document.querySelector('.form-container').style.display = 'none';

}

//document.addEventListener("DOMContentLoaded", function () {
//    const emailField = document.getElementById("email");
//    const altEmailField = document.getElementById("altEmail");
//    const form = document.getElementById("registrationForm");
//    const nxtBtn = document.getElementById("nextBtn");
//
//    altEmailField.addEventListener("input", function () {
//        validateEmailFields();
//    });
//
//    form.addEventListener("submit", function (event) {
//        if (!validateEmailFields()) {
//            event.preventDefault(); // Prevent form submission if validation fails
//        }
//    });
//
//    function validateEmailFields() {
//        const email = emailField.value.trim();
//        const altEmail = altEmailField.value.trim();
//        const errorSpan = document.getElementById("altEmailError");
//
//        if (!errorSpan) {
//            const span = document.createElement("span");
//            span.id = "altEmailError";
//            span.className = "error";
//            altEmailField.parentNode.appendChild(span);
//        }
//
//        if (email !== "" && altEmail !== "" && email === altEmail) {
//            document.getElementById("altEmailError").innerText = "Alternate email cannot be the same as primary email.";
//            document.getElementById("altEmailError").style.color = "red";
//            altEmailField.style.borderColor = "red";
//            nxtBtn.disabled = true;
//            return false;
//        } else {
//            document.getElementById("altEmailError").innerText = "";
//            altEmailField.style.borderColor = "";
//            nxtBtn.disabled = (email === "" || altEmail === "");
//            return true;
//        }
//    }
//
//});

//document.addEventListener("DOMContentLoaded", function () {
//    const accountField = document.getElementById("accNo");
//    const confirmField = document.getElementById("cnfaccNo");
//    const errorSpan = document.getElementById("accountError");
//    const nxtBtn = document.getElementById("nextBtn");
//
//    function validateAccountNumbers() {
//        const account = accountField.value.trim();
//        const confirmAccount = confirmField.value.trim();
//
//        if (account !== "" && confirmAccount !== "" && account !== confirmAccount) {
//            errorSpan.innerText = "Account numbers do not match!";
//            confirmField.style.borderColor = "red";
//            nxtBtn.disabled = true;
//        } else {
//            errorSpan.innerText = "";
//            confirmField.style.borderColor = "";
//            nxtBtn.disabled = account === "" || confirmAccount === "";
//        }
//    }
//
//    confirmField.addEventListener("paste", function (e) {
//        e.preventDefault();
//        alert("Copy-paste is not allowed. Please type manually.");
//    });
//
//    // Attach validation to input events
//    accountField.addEventListener("input", validateAccountNumbers);
//    confirmField.addEventListener("input", validateAccountNumbers);
//});

// Function to fetch brand list from API
async function fetchBrandList() {
  try {
    const response = await fetch("/api/v1/brandlist")
    const data = await response.json()
    return data
  } catch (error) {
    console.error("Error fetching brand list:", error)
    return []
  }
}

function populateBrandSelect(select, brands) {
  if (!Array.isArray(brands) || brands.length === 0) {
    console.warn("No brands available")
    select.innerHTML = '<option value="">No brands found</option>'
    return
  }

  select.innerHTML = `
        <option value="">Select Brand</option>
        ${brands.map((brand) => `<option value="${brand}">${brand}</option>`).join("")}
    `
}

// Function to toggle file upload visibility and validate form
function toggleUpload(index) {
  const association = document.getElementById(`association${index}`)
  const fileUpload = document.getElementById(`brandCertificate${index}`).parentNode
  const fileInput = document.getElementById(`brandCertificate${index}`)


  if (association.value === "trader" || association.value === "") {
    fileUpload.style.display = "none"
    fileInput.required = false
    fileInput.style.border = "none"
    // Clear any existing file when switching to trader
    fileInput.value = ""
  } else {
    fileUpload.style.display = "block"
    fileInput.required = true
    fileInput.style.border = "2px solid #ff0000"
    // Force validation since file is now required but not present
    nextBtn.disabled = true
  }


  // Always run validation after toggling
  validateForm()
}



// Function to create a new brand entry
function createBrandEntry(index, brands) {
  const container = document.createElement("div")
  container.className = "form-group brand-group"
  container.innerHTML = `
        <hr style="border-top: 2px dotted #45a049; margin: 30px 0;">
        <label for="brand${index}">Brand Name:</label>
        <select id="brand${index}" name="brands[]" class="brandSelect" onchange="validateForm()">
            <option value="">Select Brand</option>
        </select>

        <label for="association${index}">Type of Association:</label>
        <select id="association${index}" name="association[]" class="associationSelect" onchange="toggleUpload(${index})">
            <option value="">Select Association</option>
            <option value="trader">Trader / Wholesaler</option>
            <option value="authorized">Authorized Dealer / Distributor</option>
            <option value="owner">Brand Owner</option>
        </select>

        <div class="form-group file-upload-brand" style="display: none;">
            <label for="brandCertificate${index}">Upload Brand Certificate (if applicable):</label>
            <input type="file" id="brandCertificate${index}" name="brandCertificate[]" accept=".pdf,.jpg,.jpeg,.png">
        </div>

        <button type="button" class="btn-remove" onclick="removeBrandEntry(this)">
            <i class="fas fa-minus"></i> Remove
        </button>
    `

  populateBrandSelect(container.querySelector(".brandSelect"), brands)
  return container
}

// Function to remove a brand entry
function removeBrandEntry(button) {
  button.closest(".brand-group").remove()
  validateForm()
}

// Enhanced validation function
function validateForm() {
  const brandGroups = document.querySelectorAll(".brand-group")
  const nextBtn = document.getElementById("nextBtn")
  let isValid = true

  // Check each brand group for validity
  brandGroups.forEach((group) => {
    const brand = group.querySelector(".brandSelect")
    const association = group.querySelector(".associationSelect")
    const certificate = group.querySelector('input[type="file"]')

    // Check if brand is selected
    if (!brand.value) {
      isValid = false
      return
    }

    // Check if association is selected
    if (!association.value) {
      isValid = false
      return
    }

    // Check if certificate is required and present
    if (association.value !== "trader" && !certificate.files.length) {
      isValid = false
      return
    }
  })

  nextBtn.disabled = !isValid
  return isValid
}

// Main function to initialize the form
async function initForm() {
  const brandContainer = document.getElementById("brandContainer")
  const addBrandBtn = document.getElementById("addBrandBtn")
  const brands = await fetchBrandList()

  // Populate the initial brand select
  populateBrandSelect(document.getElementById("brand1"), brands)

  // Add event listener for the "Add More Brands" button
  addBrandBtn.addEventListener("click", () => {
    const index = brandContainer.children.length + 2
    const newEntry = createBrandEntry(index, brands)
    brandContainer.appendChild(newEntry)
    validateForm() // Validate after adding new entry
  })

  // Initialize the first association select and run validation
  toggleUpload(1)
  validateForm()

  // Add event listeners for form changes
  document.addEventListener("change", (e) => {
    if (e.target && (e.target.type === "file" ||
                     e.target.classList.contains("brandSelect") ||
                     e.target.classList.contains("associationSelect"))) {
      validateForm()
    }
  })

  // Add event listener for page visibility changes
  document.addEventListener("visibilitychange", () => {
    if (!document.hidden) {
      validateForm()
    }
  })

  // Run validation when the window regains focus
  window.addEventListener("focus", validateForm)
}

// Call the init function when the DOM is loaded
document.addEventListener("DOMContentLoaded", initForm)


// Initialize first section
showSection(currentSection);