<template>
  <div class="shift">
    <h3>Shifts for

      <select v-model="selectedMonth" v-on:change="getAll()">
        <option v-for="option in getMonthList(new Date().getFullYear())" v-bind:value="option.value">
          {{ option.text }}
        </option>
      </select>
    </h3>

    <div v-if="addAdjustment" class="modal-bg">
      <div class="modal-win">
        <h3>Add adjustment</h3>
        <div>
          <div class="clearfix">
            <label class="mdl-textfield_label" for="edit-item-firstName">Date</label>&nbsp;
            <select class="mdl-textfield_input" id="edit-item-firstName"  v-model="newAdjustment.adjustmentDate">
              <option v-for="day in days" v-bind:value="day.getDate()">
                {{day.getDate()}}
              </option>
            </select>
          </div>
          <div class="clearfix">
            <label class="mdl-textfield_label" for="edit-item-lastName">Sum</label>&nbsp;
            <input class="mdl-textfield_input"  type="number" id="edit-item-lastName" v-model.number="newAdjustment.adjustment">
          </div>
          <div class="clearfix">
            <label class="mdl-textfield_label" for="edit-item-salary">Note</label>&nbsp;
            <input class="mdl-textfield_input" type="text" id="edit-item-salary" v-model="newAdjustment.adjustmentNote">
          </div>
        </div>
        <section>
          <br>
          <button @click="addAdjustment = !addAdjustment">Close</button>&nbsp;
          <button id="new-item-edit" @click="createAdjustment" >Ok</button>
        </section>
        <p class="error">{{errorMessage}}</p>
      </div>
    </div>

    <table id="firstTable" class="shift">
      <thead>
      <tr>
        <th>Name</th>
        <th v-for="row in days">
          {{row.getDate()}}
        </th>
      </tr>
      </thead>
      <tbody>
      <template v-for="shift in shifts">
      <tr >
        <td  v-bind:class="{ opened: shift.contentVisible, names: true }" v-on:click="shift.contentVisible = !shift.contentVisible; getAdjustment(shift);">
          {{shift.worker.lastName}} {{shift.worker.firstName}}
        </td>
        <td v-for="day in days" v-bind:class="{filled : compareDate(day, shift.shifts),currentDay :isCurrentDay(day)}" >
          <input v-if="!compareDate(day, shift.shifts)" type=submit value="+" class="shiftButton" @click="addShift(day, shift.worker.id)">
          <input v-else type=submit value="-" class="shiftButton" @click="rmShift(day, shift.shifts, shift.worker.id)">
        </td>
        <td>{{workersSalaries[shift.worker.id]}}</td>
        <td>  <input type=submit value="+ −" class="shiftButton" @click="selectWorkerId(shift.worker.id)"> </td>
      </tr>
      <tr :class="{ opened: shift.contentVisible }"  v-for="adj in adjustments[shift.worker.id]" v-if="shift.contentVisible">
        <td>{{adj.adjustmentNote}}</td>
        <td v-for="day in days" v-if="compareAdjustmentDate(day, adj.adjustmentDate)">{{adj.adjustment}} </td>
        <td v-else></td>
      </tr>
        <tr v-if="shift.contentVisible"></tr>
      </template>
      </tbody>
    </table>


  </div>
</template>

<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator'
    import axios from 'axios';
    import {Worker} from './Worker.vue';

    export interface ShiftUser {
    worker: Worker;
    shiftDates: Shift[];
  }
  export class Shift{
    id!: number;
    worker!: Worker;
    shiftDate!: string;
  }
  export class Salary{
    id?: number;
    worker?: Worker;
    salary?: number;
    salaryDate?: string;
  }

  export class Adjustment{
    id?: number;
    worker?: Worker;
    adjustment?: string;
    adjustmentDate?: string;
    adjustmentNote?: string;
  }

  @Component({
    components: {}
  })

  export default class Shifts extends Vue {
    public shifts: ShiftUser[] = [];
    public selectedMonth: number = -1;
    public days: Date[] = [];
    public errorMessage:string = "";
    public workersSalaries: { [key: number]: any; } = {};
    public adjustments: { [key: number]: Adjustment[]; } = {};
    public addAdjustment : boolean = false;
    public newAdjustment : Adjustment = {};
    public workerIdForAdj : number = 0;

    private async created() {
      await this.getAll();
    }

    private async getAll(){
      let month = localStorage.getItem("selectedMonth");
      console.log(month);
      if (month == null) {
        this.selectedMonth = this.getCurrentMonth();
        localStorage.setItem("selectedMonth", this.selectedMonth + "");
      } else if(this.selectedMonth == -1){
        this.selectedMonth = Number(month);
      } else if (this.selectedMonth != Number(month)) {
        localStorage.setItem("selectedMonth", this.selectedMonth + "");
      }

      this.getShifts().then(response => {
        this.shifts = response;
      });

      this.getSalaries().then(data => {
        console.log('work sal response', data);
        this.workersSalaries = data;
      });
    }

    private getCurrentMonth(){
      return new Date().getMonth() +1;
    }

    private async getSalary(workerId : any) {
      let workerSalary: Salary[] = [];
      const response = await axios.get(
        '/api/salaries?worker=' + Number(workerId) + "&date=" + [new Date().getFullYear(), ("0" +this.selectedMonth).slice(-2), '01'].join('-'));
      workerSalary = response.data;
      this.workersSalaries[Number(workerId)] = workerSalary[0].salary;
    }

    private async getAdjustment(shift : any) {
      if (!shift.contentVisible) {
       this.adjustments[shift.worker.id] = [];
       return ;
      }
      const temp : { [key: number]: Adjustment[]; } = {};
      const response = await axios.get(
          '/api/salaries_adjustment?worker=' + shift.worker.id + '&date=' + [new Date().getFullYear(), ("0" + this.selectedMonth).slice(-2), '01'].join('-'));
      let adjustments = response.data;
      let adjustment : Adjustment;
      for (adjustment of adjustments) {
        let array: Adjustment[] = [];
        let id: number = Number(adjustment.worker!.id);
        if (temp[id] == null) {
          temp[id] = array;
        } else {
          array = temp[id];
        }
        array.push(adjustment);
      }
      this.adjustments = temp;
    }

    private async getSalaries(){
      const temp = {...this.workersSalaries};
      const response = await axios.get(
        '/api/salaries?date=' + [new Date().getFullYear(), ("0" +this.selectedMonth).slice(-2), '01'].join('-'));
      let workerSalaries = response.data;
      var  workerSalary : Salary = {};
      for (workerSalary of workerSalaries) {
        let id : number = Number(workerSalary.worker!.id);
        if (workerSalary.salary == null) {
          temp[id] = 0;
          continue;
        }
        temp[id] = workerSalary.salary;
      }
      return temp;
    }

    private async getShifts() {
      this.getDaysInMonth(this.selectedMonth -1, new Date().getFullYear())
      const response = await axios.get('/api/shifts/' +
        [new Date().getFullYear(), ("0" +this.selectedMonth).slice(-2), '01'].join('-'));
      return response.data;
    }

    private compareDate(day: Date, shifts: Shift[]) {
      if (shifts && shifts.length && shifts[0].shiftDate === null) {
          return false;
      }

      var shift;
      for (shift of shifts) {
        if (day.getDate() == new Date(shift.shiftDate).getDate()) {
          return true;
        }
      }
      return false
    }

    private compareAdjustmentDate(day: Date, date: string) {
      if (date === null) {
        return false;
      }
      if (day.getDate() == new Date(date).getDate()) {
          return true;
      }
      return false
    }

    private isCurrentDay(day: Date) {
      return new Date().getDate() == day.getDate() && new Date().getMonth() == day.getMonth();
    }

    private getMonthList(year: number) {
      let i;
      let months = [];
      for (i = 0; i < 12; i++) {
        months.push(new Option(new Date(year, i, 1).toLocaleString('en-us', {month: 'long'}), i + 1 + ''))
      }
      return months;
    }

    private async createAdjustment(){
        var adjustment = this.newAdjustment;
        let worker: Worker = {};
        worker.id = this.workerIdForAdj;
        console.log( this.workerIdForAdj);
        adjustment.worker = worker;
        adjustment.adjustmentDate =  new Date().getFullYear() + "-" + ("0" + (this.selectedMonth  )).slice(-2)
            + "-" + ("0" + this.newAdjustment.adjustmentDate).slice(-2);

        await axios.post('/api/salaries_adjustment', adjustment)
            .then(() => {
                Promise.all([ this.getSalary(worker.id)])
                }
            ).catch(error => {
                this.errorMessage = error.response.data.message
            });
        this.addAdjustment = !this.addAdjustment
        this.newAdjustment = {};
    }

    private async addShift(day : Date, workerId: number) {
      var shift = new Shift();
      console.log(day.getMonth());

      shift.shiftDate = day.getFullYear() + "-" + ("0" + (day.getMonth()+1)).slice(-2) + "-" + ("0" + day.getDate()).slice(-2);
      let worker: Worker = {};
      worker.id = workerId;
      shift.worker = worker;
      await axios.post('/api/shifts', shift)
        .then(() => {
          Promise.all([this.getShifts(), this.getSalary(worker.id)]).then(data =>
          this.shifts = data[0]);
          }
        ).catch(error => {
          this.errorMessage = error.response.data.message
        });
      console.log(workerId);
    }

    private async rmShift(day: Date, shifts: Shift[], workerId: number) {
      var shift = new Shift();
      for (shift of shifts) {
        if (day.getDate() == new Date(shift.shiftDate).getDate()) {
          break;
        }
      }
      await axios.delete('/api/shifts/' + shift.id)
        .then(() => {
          Promise.all([this.getShifts(), this.getSalary(workerId)]).then(data =>
            this.shifts = data[0]);
          }
        ).catch(error => {
          this.errorMessage = error.response.data.message
        });
      console.log(workerId);
    }

    private getDaysInMonth(month: number, year: number) {
      // Since no month has fewer than 28 days
      var date = new Date(year, month, 1);
      this.days = [];
      while (date.getMonth() === month) {
        this.days.push(new Date(date));
        date.setDate(date.getDate() + 1);
      }
    }

      private selectWorkerId(workerId: number){
          this.addAdjustment = !this.addAdjustment;
          console.log(workerId);
          this.workerIdForAdj = workerId;
      }


  }

</script>
<style>
  table.shift {
    font-family: 'Open Sans', sans-serif;
    width: 60%;
    border-collapse: collapse;
    margin: 0 auto;
    border-spacing: 0;
  }

  table.shift th {
    padding: 5px;
    min-width: 10px;
    text-align: center;
  }

  tr:hover {
    background-color: #f5f5f5;
  }

  table.shift td {
    padding: 1px;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    border-top: 1px solid rgba(170, 179, 232, 0.17);
    border-right: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 25px;
    text-align: left;
    height: 28px;
  }

  table.shift td:hover {
    background-color: #42b983;
  }

  td.currentDay {
    background-color: #ff6500;
  }

  td.filled {
    background-color: #206600;
  }

  input.shiftButton {
    align-self: center;
    background: transparent;
    border: 1px salmon;
    text-align: center;
    font-size: 13px;
    text-decoration: none;
    transition-duration: 0.4s;
    width: 100%;
    height: 100%;
  }

  .opened {
    background-color: #dddddd;
  }
  table.shift td.names {
    min-width: 150px;
    text-align: left;
    min-height: 40px;
  }

  .modal-bg {
    /*background-color: rgba(0,0,0, 0.5);*/
    position: absolute;
    top: 0;
    left: 0;
    height: 50vh;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0px auto 10px auto;
  }

  .modal-win {
    border: 1px lightslategrey solid;
    background-color: white;
    border-radius: .25rem;
    padding: 1rem;
    width: 25%;
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
