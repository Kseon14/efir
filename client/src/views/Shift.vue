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
        <td v-for="day in days">
          <div v-if="compareDate(day, row.shiftDates)">
            <div class="cell">&nbsp;</div>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import axios from 'axios';
  import Worker from 'Worker.vue';

  export interface Shift {
    id: number;
    worker: Worker;
    shiftDates: string[];
  }

  @Component({
    components: {}
  })

  export default class Shifts extends Vue {
    public shifts: Shift[] = [];
    public selectedMonth: number = new Date().getMonth() +1;
    public days: Date[] = [];

    private async created() {
      this.getShifts();
    }

    private async getShifts() {
      this.getDaysInMonth(this.selectedMonth -1, new Date().getFullYear())
      console.log(this.selectedMonth)
      const response = await axios.get('/api/shifts/' + [new Date().getFullYear(), this.selectedMonth, '01'].join('-'));
      this.shifts = await response.data;
    }

    private compareDate(day: Date, days: string[]) {
      var dayIncome;
      for (dayIncome of days) {
        if (day.getDate() == new Date(dayIncome).getDate()) {
          return true;
        }
      }
      return false
    }

    private getMonthList(year: number) {
      let i;
      let monthes = [];
      for (i = 0; i < 12; i++) {
        monthes.push(new Option(new Date(year, i, 1).toLocaleString('en-us', {month: 'long'}), i + 1 + ''))
      }
      return monthes;
    }

    private getDaysInMonth(month: number, year: number) {
      // Since no month has fewer than 28 days
      var date = new Date(year, month, 1);
      this.days = [];
      while (date.getMonth() === month) {
        this.days.push(new Date(date));
        date.setDate(date.getDate() + 1);
      }
      console.log(this.days)
    }
  }

</script>
<style>
  table.shift {
    font-family: 'Open Sans', sans-serif;
    width: 80%;
    border-collapse: collapse;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    margin: 0 auto;

  }

  table.shift th {
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    padding: 5px;
    min-width: 20px;
    text-align: center;
  }

  tr:hover {
    background-color: #f5f5f5;
  }

  table.shift td {
    padding: 1px;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    border-right: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 20px;
    text-align: left;
  }

  table.shift td:hover {
    background-color: #42b983;
  }

  div.cell {
    background-color: #206600;
    padding-left: 0px;
    padding-right: 0px;
    padding-top: 0px;
    padding-bottom: 0px;
  }
  div.currentDay {
    background-color: #ff6500;
    padding-left: 0px;
    padding-right: 0px;
    padding-top: 0px;
    padding-bottom: 0px;
  }

</style>
