<template>
  <div class="workers">
    <h3>Workers</h3>
    <section id="addWorker">
      <button @click="modal = !modal">Add</button>
      <br>
      <div v-if="modal" class="modal-bg">
        <div class="modal-win">
          <h3>New Worker</h3>
          <div >
            <div>
              <label class="mdl-textfield__label" for="new-item-firstName">First Name</label>
              <input class="mdl-textfield__input" type="text" id="new-item-firstName" v-model="newWorker.firstName">
            </div>
            <div>
              <label class="mdl-textfield__label" for="new-item-lastName">Last Name</label>
              <input class="mdl-textfield__input" type="text" id="new-item-lastName" v-model="newWorker.lastName">
            </div>
            <div>
              <label class="mdl-textfield__label" for="new-item-salary">Base salary</label>
              <input class="mdl-textfield__input" type="number" id="new-item-salary" v-model.number="newWorker.baseSalary">
            </div>
          </div>
          <section>
          <button @click="modal = !modal">Close</button>
          <button id="new-item-add" @click="createWorker">Add</button>
            <p>{{newWorker}}</p>
          </section>
        </div>
      </div>

    </section>
      <table id="firstTable" class="worker">
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
        <tr v-for="row in workers">
          <td>{{row.id}}</td>
          <td>{{row.lastName}} {{row.firstName}}</td>
          <td>{{row.baseSalary}}</td>
          <td>{{row.status}}</td>
          <th><button @click="deleteWorker(row.id)">del</button></th>
        </tr>
        </tbody>
      </table>
    </div>

</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import axios from 'axios';

  export interface Worker {
  id ?: number;
  firstName ?: string;
  lastName ?: string;
  baseSalary ?: number;
  status ?: string;
}

@Component({
  components: {
  }
})

export default class Workers extends Vue {
  public workers: Worker[] = [];
  public newWorker:Worker = {
    status: 'ACTIVE'
  };

  private async getWorkers(){
    const response = await axios.get('/api/workers');
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
  private createWorker(){
    axios.post('/api/workers', this.newWorker);
    this.getWorkers();
    this.modal = false
}
private deleteWorker(idUser:any){
  axios.delete('/api/workers/'+idUser);
  this.getWorkers();
}

  public modal: boolean = false;

}


</script>
<style>
  button {
    background-color: white;
    border-radius: .25rem;
    color: black;
    margin-bottom: 2em;
    border: 2px solid #008CBA;
    padding: 10px 30px;
    text-align: center;
    font-size: 13px;
    text-decoration: none;
    display: inline-block;
    transition-duration: 0.4s;
  }
  button:hover {
    background-color: #008CBA;
    color: white;
  }
  .modal-bg{
    /*background-color: rgba(0,0,0, 0.5);*/
    position: absolute;
    top: 0;
    left: 0;
    height: 100vh;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .modal-win{
    border: 1px lightslategrey solid;
    background-color: white;
    border-radius: .25rem;
    padding: 1rem;
    width: 33.33333%;
  }
  table.worker {
    font-family: 'Open Sans', sans-serif;
    width: 40%;
    border-collapse: collapse;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    margin: 0 auto;
  }

  table.worker th {
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    padding: 5px;
    min-width: 20px;
    text-align: left;

  }

  table.worker td{
    padding: 5px;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 20px;
    text-align: left;
  }



  .mdl-textfield__input{
    border: 0;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 20px;
    padding: 5px;

  }
  input:focus{
    outline: none;
  }

</style>
