package com.cs446.petpal.views

import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import com.cs446.petpal.models.Pet
import com.cs446.petpal.viewmodels.PetspageViewModel

@Composable
fun petsPopup(currPet: Pet?, currPetId: String?, popupType: String, petsViewModel: PetspageViewModel = viewModel()): Boolean {
    var showDialog by remember { mutableStateOf(true) }
    var userInputName by remember { mutableStateOf(currPet?.name?.value ?: "") }
    var userInputGender by remember { mutableStateOf(currPet?.gender?.value ?: "") }
    var userInputAnimal by remember { mutableStateOf(currPet?.animal ?: "") }
    var userInputBreed by remember { mutableStateOf(currPet?.breed ?: "") }
    var userInputbirthday by remember { mutableStateOf(currPet?.birthday ?: "") }
    var userInputAge by remember { mutableIntStateOf(currPet?.age?.value ?: 0) }
    var userInputWeight by remember { mutableDoubleStateOf(currPet?.weight?.value ?: 0.0) }
    var userInputInsuranceProvider by remember { mutableStateOf(currPet?.insuranceProvider?.value ?: "") }
    var userInputInsurancePolicyNumber by remember { mutableStateOf(currPet?.policyNumber?.value ?: "") }
    var userInputMedicationName by remember { mutableStateOf(currPet?.medicationName?.value ?: "") }
    var userInputMedicationDosage by remember { mutableStateOf(currPet?.medicationDosage?.value ?: "") }

    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = {
            Text(text = if (popupType == "ADD") "Add Pet" else "Edit Pet")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = userInputName,
                    onValueChange = { userInputName = it },
                    label = { Text("Pet Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = userInputAnimal,
                            onValueChange = { userInputAnimal = it },
                            label = { Text("Animal Type") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = userInputBreed,
                            onValueChange = { userInputBreed = it },
                            label = { Text("Breed") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = userInputGender,
                            onValueChange = { userInputGender = it },
                            label = { Text("Gender") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = if (userInputWeight == 0.0) "" else userInputWeight.toString(),
                            onValueChange = {
                                userInputWeight = it.toDoubleOrNull() ?: 0.0
                            },
                            label = { Text("Weight") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = userInputbirthday,
                            onValueChange = { userInputbirthday = it },
                            label = { Text("Birthday") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = if (userInputAge == 0) "" else userInputAge.toString(),
                            onValueChange = { userInputAge = it.toIntOrNull() ?: 0 },
                            label = { Text("Age") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = userInputInsuranceProvider,
                            onValueChange = { userInputInsuranceProvider = it },
                            label = { Text("Insurance Provider") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = userInputInsurancePolicyNumber,
                            onValueChange = { userInputInsurancePolicyNumber = it },
                            label = { Text("Policy Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = userInputMedicationName,
                            onValueChange = { userInputMedicationName = it },
                            label = { Text("Medication") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = userInputMedicationDosage,
                            onValueChange = { userInputMedicationDosage = it },
                            label = { Text("Dosage") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialog = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Black,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    showDialog = false
                    if (popupType == "ADD") {
                        petsViewModel.createPetForUser(
                            userInputName,
                            userInputAnimal,
                            userInputBreed,
                            userInputGender,
                            userInputbirthday,
                            userInputAge,
                            userInputWeight,
                            userInputInsuranceProvider,
                            userInputInsurancePolicyNumber,
                            userInputMedicationName,
                            userInputMedicationDosage
                        ) { success, _ -> }
                    }
                    else {
                        // TODO: uncomment this once the Edit function is implemented
                        /* if (currPetId != null) {
                            petsViewModel.updatePetForUser(
                                currPetId
                                userInputName,
                                userInputGender,
                                userInputbirthday,
                                userInputAge,
                                userInputWeight,
                                userInputInsuranceProvider,
                                userInputInsurancePolicyNumber,
                                userInputMedicationName,
                                userInputMedicationDosage
                            ) { success, _ -> }
                        } */
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(
                    text = "Ok",
                    color = Color.Black,
                )
            }
        }
    )
    return showDialog
}
