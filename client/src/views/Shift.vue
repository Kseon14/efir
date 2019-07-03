<template>
  <div class="shift">
    <h3>Shifts for

      <select v-model="selectedMonth" v-on:change="getAll()">
        <option v-for="option in getMonthList(new Date().getFullYear())" v-bind:value="option.value">
          {{ option.text }}
        </option>
      </select>
    </h3>

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
      <tr v-for="row in shifts">
        <td>{{row.worker.lastName}} {{row.worker.firstName}}</td>
        <td v-for="day in days" v-bind:class="{filled : compareDate(day, row.shifts),currentDay :isCurrentDay(day)}" >
          <input v-if="!compareDate(day, row.shifts)" type=submit value="+" class="shiftButton" @click="addShift(day, row.worker.id)">
          <input v-if="compareDate(day, row.shifts)" type=submit value="-" class="shiftButton" @click="rmShift(day, row.shifts, row.worker.id)">
        </td>
        <td>{{workersSalaries[row.worker.id]}}</td>
      </tr>
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

  @Component({
    components: {}
  })

  export default class Shifts extends Vue {
    public shifts: ShiftUser[] = [];
    public selectedMonth: number = this.getCurrentMonth();
    public days: Date[] = [];
    public errorMessage:string = "";
    public workersSalaries: { [key: number]: any; } = {};

    private getCurrentMonth(){
      return new Date().getMonth() +1;
    }

    private async created() {
      Promise.all([this.getShifts(), this.getSalaries()]).then( data =>
        this.shifts = data[0]
      )
    }

    private async getSalary(workerId : any) {
      let workerSalary: Salary[] = [];
      const response = await axios.get(
        '/api/salaries?worker=' + Number(workerId) + "&date=" + [new Date().getFullYear(), ("0" +this.selectedMonth).slice(-2), '01'].join('-'));
      workerSalary = response.data;
      this.workersSalaries[Number(workerId)] = workerSalary[0].salary;
    }

    private async getAll(){
      Promise.all([this.getShifts(), this.getSalaries()]).then( data =>
        this.shifts = data[0]
      )
    }

    private async getSalaries(){
      let workerSalaries:Salary[] = [];
      const response = await axios.get(
        '/api/salaries?date=' + [new Date().getFullYear(), ("0" +this.selectedMonth).slice(-2), '01'].join('-'));
      workerSalaries = response.data;
      var  workerSalary : Salary = {};
      for (workerSalary of workerSalaries) {
        let id : number = Number(workerSalary.worker!.id);
        if (workerSalary.salary == null) {
          this.workersSalaries[id] = 0;
          continue;
        }
        this.workersSalaries[id] = workerSalary.salary;
        console.log(workerSalary.salary);
        console.log(workerSalary.salaryDate);
      }
      console.log(this.workersSalaries);
    }

    private async getShifts() {
      this.getDaysInMonth(this.selectedMonth -1, new Date().getFullYear())
      const response = await axios.get('/api/shifts/' +
        [new Date().getFullYear(), ("0" +this.selectedMonth).slice(-2), '01'].join('-'));

      return response.data;
    }

    private compareDate(day: Date, shifts: Shift[]) {
      if (shifts == null) {
        console.log("shift is null")
        return false;
      }
      if (shifts[0].shiftDate == null) {
          console.log("shiftDate is null")
          return false;
      }
      var shift;
      for (shift of shifts) {
        if (day.getDate() == new Date(shift.shiftDate).getDate()) {
          console.log("shift exist for specific date: " + new Date(shift.shiftDate).getDate() + " and worker " + shift.worker.id);
          return true;
        }
      }
      console.log("shifts not exists for dates ");
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

    private async addShift(day : Date, workerId: number) {
      var shift = new Shift();
     // day.setDate(day.getDate());
     // console.log(day.getFullYear() + "-" + "0" + day.getMonth() + "-" + day.getDate());
      console.log(day.getMonth())

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
  }

</script>
<style>
  table.shift {
    font-family: 'Open Sans', sans-serif;
    width: 80%;
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
    height: 25px;
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


</style>
