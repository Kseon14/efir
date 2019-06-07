<template>
  <div class="shift">
    <h3>Shifts for

      <select v-model="selectedMonth" v-on:change="getShifts()">
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
          <input v-if="compareDate(day, row.shifts)" type=submit value="-" class="shiftButton" @click="rmShift(day, row.shifts)">
        </td>
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


  @Component({
    components: {}
  })

  export default class Shifts extends Vue {
    public shifts: ShiftUser[] = [];
    public selectedMonth: number = new Date().getMonth() +1;
    public days: Date[] = [];
    public errorMessage:string = "";

    private async created() {
      this.getShifts();
    }

    private async getShifts() {
      this.getDaysInMonth(this.selectedMonth -1, new Date().getFullYear())
      const response = await axios.get('/api/shifts/' +
        [new Date().getFullYear(), this.selectedMonth, '01'].join('-'));
      this.shifts = await response.data;
    }

    private compareDate(day: Date, shifts: Shift[]) {
      if (shifts && shifts.length == 1) {
        if (shifts[0].shiftDate == null) {
          return false;
        }
      }
      var shift;
      for (shift of shifts) {
        if (day.getDate() == new Date(shift.shiftDate).getDate()) {
          return true;
        }
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

    private async addShift(day : Date, workerId: number) {
      var shift = new Shift();
      day.setDate(day.getDate() +1 )
      console.log(day.toISOString())
      shift.shiftDate = day.toISOString();
      let worker: Worker = {};
      worker.id = workerId
      shift.worker = worker;
      await axios.post('/api/shifts', shift)
        .then(() => {
          this.getShifts();
          }
        ).catch(error => {
          this.errorMessage = error.response.data.message
        });
    }

    private async rmShift(day: Date, shifts: Shift[]) {
      var shift = new Shift();
      for (shift of shifts) {
        if (day.getDate() == new Date(shift.shiftDate).getDate()) {
          break;
        }
      }
      await axios.delete('/api/shifts/' + shift.id)
        .then(() => {
          this.getShifts();
          }
        ).catch(error => {
          this.errorMessage = error.response.data.message
        });
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

  td.filled {
    background-color: #206600;
  }

  td.currentDay {
    background-color: #ff6500;
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
