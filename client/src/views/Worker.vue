<template>
  <div class="workers">
    <h3>Workers</h3>
    <section id="addWorker">
      <button @click="modalCreate = !modalCreate">add</button>
      <br>
      <br>
      <div v-if="modalCreate" class="modal-bg">
        <div class="modal-win">
          <h3>New Worker</h3>
          <div>
            <div class="clearfix">
              <label class="mdl-textfield_label" for="new-item-firstName">First Name</label>&nbsp;
              <input class="mdl-textfield_input" type="text" id="new-item-firstName" v-model="newWorker.firstName">
            </div>
            <div class="clearfix">
              <label class="mdl-textfield_label" for="new-item-lastName">Last Name</label>&nbsp;
              <input class="mdl-textfield_input"  type="text" id="new-item-lastName" v-model="newWorker.lastName">
            </div>
            <div class="clearfix">
              <label class="mdl-textfield_label" for="new-item-salary">Base salary</label>&nbsp;
              <input class="mdl-textfield_input" type="number" id="new-item-salary" v-model.number="newWorker.baseSalary">
            </div>
          </div>
          <section>
            <br>
            <button  v-on:click="modalCreate = !modalCreate; newWorker ={}">Close</button>&nbsp;
            <button id="new-item-add" @click="createWorker">Add</button>
          </section>
          <p class="error">{{errorMessage}}</p>
        </div>
      </div>

      <div v-if="modalEdit" class="modal-bg">
        <div class="modal-win">
          <h3>Edit Worker</h3>
          <div>
            <div class="clearfix">
              <label class="mdl-textfield_label" for="edit-item-firstName">First Name</label>&nbsp;
              <input class="mdl-textfield_input" type="text" id="edit-item-firstName" v-model="selectedWorker.firstName">
            </div>
            <div class="clearfix">
              <label class="mdl-textfield_label" for="edit-item-lastName">Last Name</label>&nbsp;
              <input class="mdl-textfield_input"  type="text" id="edit-item-lastName" v-model="selectedWorker.lastName">
            </div>
            <div class="clearfix">
              <label class="mdl-textfield_label" for="edit-item-salary">Base salary</label>&nbsp;
              <input class="mdl-textfield_input" type="number" id="edit-item-salary" v-model.number="selectedWorker.baseSalary">
            </div>
            <div class="clearfix">
              <label class="mdl-textfield_label" for="edit-item-status">Status</label>&nbsp;
              <select  class="mdl-textfield_input" id="edit-item-status" v-model.number="selectedWorker.status">
                <option>ACTIVE</option>
                <option>INACTIVE</option>
              </select>
            </div>
          </div>
          <section class="buttons">
            <br>
            <button @click="modalEdit = !modalEdit">Close</button>&nbsp;
            <button id="new-item-edit" @click="editWorker">Ok</button>
          </section>
          <p class="error">{{errorMessage}}</p>
        </div>
      </div>

    </section>
    <div class="over">
    <table id="workrTable" class="worker">
      <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Base salary</th>
        <th>Status</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="row in workers" v-on:change="this.getWorkers()">
        <td>{{row.id}}</td>
        <td>{{row.lastName}} {{row.firstName}}</td>
        <td>{{row.baseSalary}}</td>
        <td>{{row.status}}</td>
        <th>
          <button @click="selectWorker(row)">edit</button>&nbsp;
          <button @click="deleteWorker(row.id)">delete</button>
        </th>
      </tr>
      </tbody>
    </table>
    </div>
  </div>

</template>

<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator'
    import axios from 'axios';

    export interface Worker {
    id?: number;
    firstName?: string;
    lastName?: string;
    baseSalary?: number;
    status?: string;
  }

  @Component
  export default class Workers extends Vue {
    public workers: Worker[] = [];
    public newWorker: Worker = {};
    public selectedWorker = {};
    public errorMessage:string = "";

    private async getWorkers() {
      let response = await axios.get('/api/workers');
      this.workers = await response.data;
    }

    // private validateName() {
    // if (!this.newWorker.firstName.length) {
    // return (#new-item-add).disabled = true;
    // }
    // };
    private async created() {
      this.getWorkers()
    }

    private async createWorker() {
      try {
        if (this.newWorker.firstName == "") throw "is empty";
      }catch (e) {
        this.errorMessage = e
      }
      this.newWorker.status = 'ACTIVE';
      await axios.post('/api/workers', this.newWorker)
        .then(()=> {
          this.getWorkers();
          this.modalCreate = false;
          this.newWorker = {};
        }
      ).catch(error => {this.errorMessage = error.response.data.message});
        this.newWorker = {};
    }

    private async deleteWorker(idUser: any) {
      if (confirm("Do you really want to delete?")) {
        await axios.delete('/api/workers/' + idUser);
        this.getWorkers();
      }
    }

    private async editWorker() {
      await axios.put('/api/workers', this.selectedWorker)
        .then(() => {
            this.getWorkers();
            this.modalEdit = false
          }
        ).catch(error => {
          this.errorMessage = error.response.data.message
        });
    }

    public modalCreate : boolean = false;
    public modalEdit: boolean = false;

    private selectWorker(worker: Worker){
      this.modalEdit = !this.modalEdit;
      Object.assign(this.selectedWorker, worker);
    }
  }


</script>
<style>
  button {
    align-self: center;
    background-color: white;
    border-radius: .25rem;
    color: black;
    border: 1px solid #42b983;
    padding: 5px 15px;
    text-align: center;
    font-size: 13px;
    text-decoration: none;
    display: inline-block;
    transition-duration: 0.4s;
  }

  button:hover {
    background-color: #42b983;
    color: white;
  }

  .modal-bg {
    /*background-color: rgba(0,0,0, 0.5);*/
    position: fixed;
    top: 25%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .over{
    overflow: auto;
  }

  .modal-win {
    border: 1px lightslategrey solid;
    background-color: white;
    border-radius: .25rem;
    padding: 20px;
    width: 300px;
    max-width: 100%;
    align-self: center;
  }

  table.worker {
    font-family: 'Open Sans', sans-serif;
    width: 40%;
    min-width:600px;
    border-collapse: collapse;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    margin-left:auto;
    margin-right:auto;
  }

  table.worker th {
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    padding: 3px;
    min-width: 20px;
    text-align: left;
  }

  table.worker td {
    padding: 3px;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 20px;
    text-align: left;
  }

  .mdl-textfield_input {
    border: 0;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    padding: 5px;
    display: inline-block;
    float: left;
    text-align: left;
  }

  .mdl-textfield_label {
    border: 0;
    min-width: 12px;
    display: inline-block;
    padding: 5px;
    float: left;
    clear: left;
    width: 100px;
    text-align: left;
  }

  input:focus {
    outline: none;
    border: 0;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 20px;
    padding: 5px;
  }

  p.error {
    color: red;
    max-width:100%;
  }

  .clearfix:before {
    display: table;
    content: "";
  }

  .clearfix:after {
    display: table;
    clear:both;
    content: "";
  }

</style>
