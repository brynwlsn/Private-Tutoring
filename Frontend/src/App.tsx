import { useState, useMemo, useEffect } from "react";
import {
  BookOpen,
  Calendar,
  Users,
  GraduationCap,
  Bell,
  Search,
  ChevronDown,
  Plus,
  X,
  Check,
  Clock,
  User,
  Phone,
  Mail,
  Edit2,
  Trash2,
  LayoutDashboard,
  UserCheck,
  LogOut,
  ChevronLeft,
  ChevronRight,
  MoreHorizontal,
  Filter,
  AlertTriangle,
  CheckCircle2,
  BookMarked,
  Layers,
  Shield,
  ArrowUpDown,
  SlidersHorizontal,
  Download,
} from "lucide-react";
import React from "react";

// ─── Types ───────────────────────────────────────────────────────────────────
type Role = "student" | "teacher" | "admin";

interface Jenjang {
  id: number;
  nama: string;
}
interface Mapel {
  id: number;
  nama: string;
}
interface Guru {
  id: number;
  nama: string;
  email: string;
  no_hp: string;
  password?: string;
  id_admin: number;
  avatar?: string;
}
interface Siswa {
  id: number;
  nama: string;
  email: string;
  no_hp: string;
  tanggal_lahir: string;
  jenis_kelamin: string;
  password?: string;
  id_jenjang: number;
  alamat?: string;
}
interface Admin {
  id: number;
  nama: string;
  email: string;
  no_hp: string;
  password?: string;
}
interface KeahlianGuru {
  id: number;
  id_guru: number;
  id_mapel: number;
  id_jenjang: number;
}
interface JadwalKesediaan {
  id: number;
  hari: string;
  jam_mulai: string;
  jam_selesai: string;
  status: string;
  id_guru: number;
  id_admin: number;
}
// Data Les yang sudah di-join dari Daftar_les dan Detail_Daftar_Les oleh Backend
interface Les {
  id: number;
  tanggal_mulai: string;
  tanggal_selesai: string;
  id_siswa: number;
  id_jadwal: number;
  durasi: number;
  id_mapel: number;
  id_jenjang: number;
  // Tambahkan dua baris ini:
  jam_mulai?: string;
  jam_selesai?: string;
}

// ─── Mock Data ────────────────────────────────────────────────────────────────
const JENJANG: Jenjang[] = [
  { id: 1, nama: "SD" },
  { id: 2, nama: "SMP" },
  { id: 3, nama: "SMA" },
];

const MAPEL: Mapel[] = [
  { id: 1, nama: "Mathematics" },
  { id: 2, nama: "Indonesian" },
  { id: 3, nama: "Science" },
  { id: 4, nama: "English" },
  { id: 5, nama: "Physics" },
  { id: 6, nama: "Chemistry" },
  { id: 7, nama: "Biology" },
];

const GURU: Guru[] = [
  {
    id: 1,
    nama: "Dr. Ayu Rahmawati",
    email: "ayu@tutor.id",
    no_hp: "081234567801",
    id_admin: 1,
    avatar: "AR",
  },
  {
    id: 2,
    nama: "Budi Santoso, S.Pd",
    email: "budi@tutor.id",
    no_hp: "081234567802",
    id_admin: 1,
    avatar: "BS",
  },
  {
    id: 3,
    nama: "Citra Dewi, M.Si",
    email: "citra@tutor.id",
    no_hp: "081234567803",
    id_admin: 1,
    avatar: "CD",
  },
  {
    id: 4,
    nama: "Dian Pratama, S.T",
    email: "dian@tutor.id",
    no_hp: "081234567804",
    id_admin: 1,
    avatar: "DP",
  },
];

const SISWA: Siswa[] = [
  {
    id: 1,
    id_jenjang: 3,
    nama: "Rani Kusuma",
    tanggal_lahir: "2007-04-12",
    jenis_kelamin: "P",
    no_hp: "082111111101",
    email: "rani@mail.com",
  },
  {
    id: 2,
    id_jenjang: 2,
    nama: "Farhan Adli",
    tanggal_lahir: "2009-08-22",
    jenis_kelamin: "L",
    no_hp: "082111111102",
    email: "farhan@mail.com",
  },
  {
    id: 3,
    id_jenjang: 3,
    nama: "Sinta Maharani",
    tanggal_lahir: "2006-11-03",
    jenis_kelamin: "P",
    no_hp: "082111111103",
    email: "sinta@mail.com",
  },
  {
    id: 4,
    id_jenjang: 1,
    nama: "Kevin Wijaya",
    tanggal_lahir: "2013-01-17",
    jenis_kelamin: "L",
    no_hp: "082111111104",
    email: "kevin@mail.com",
  },
  {
    id: 5,
    id_jenjang: 2,
    nama: "Nadia Putri",
    tanggal_lahir: "2010-06-30",
    jenis_kelamin: "P",
    no_hp: "082111111105",
    email: "nadia@mail.com",
  },
];

const ADMINS: Admin[] = [
  {
    id: 1,
    nama: "Reza Firmansyah",
    email: "reza@tutor.id",
    no_hp: "081300000001",
  },
];

const KEAHLIAN: KeahlianGuru[] = [
  { id: 1, id_guru: 1, id_mapel: 1, id_jenjang: 3 },
  { id: 2, id_guru: 1, id_mapel: 5, id_jenjang: 3 },
  { id: 3, id_guru: 2, id_mapel: 1, id_jenjang: 2 },
  { id: 4, id_guru: 2, id_mapel: 3, id_jenjang: 2 },
  { id: 5, id_guru: 3, id_mapel: 6, id_jenjang: 3 },
  { id: 6, id_guru: 4, id_mapel: 1, id_jenjang: 1 },
];

// Jadwal slot per jam — id_guru langsung (tidak pakai id_keahlian)
const JADWAL: JadwalKesediaan[] = [
  {
    id: 101,
    id_guru: 1,
    hari: "Monday",
    jam_mulai: "14:00",
    jam_selesai: "15:00",
    status: "tersedia",
    id_admin: 1,
  },
  {
    id: 102,
    id_guru: 1,
    hari: "Monday",
    jam_mulai: "15:00",
    jam_selesai: "16:00",
    status: "tersedia",
    id_admin: 1,
  },
  {
    id: 103,
    id_guru: 1,
    hari: "Wednesday",
    jam_mulai: "13:00",
    jam_selesai: "14:00",
    status: "tersedia",
    id_admin: 1,
  },
  {
    id: 104,
    id_guru: 2,
    hari: "Tuesday",
    jam_mulai: "10:00",
    jam_selesai: "11:00",
    status: "tersedia",
    id_admin: 1,
  },
  {
    id: 105,
    id_guru: 3,
    hari: "Thursday",
    jam_mulai: "08:00",
    jam_selesai: "09:00",
    status: "terisi",
    id_admin: 1,
  },
];

const LES_DATA: Les[] = [
  {
    id: 4,
    id_siswa: 5,
    id_jadwal: 105,
    id_mapel: 6,
    id_jenjang: 3,
    tanggal_mulai: "2025-01-23T08:00",
    tanggal_selesai: "2025-01-23T09:00",
    durasi: 60,
  },
];

const HARI = [
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
  "Sunday",
];
const DAY_SHORT: Record<string, string> = {
  Monday: "Mon",
  Tuesday: "Tue",
  Wednesday: "Wed",
  Thursday: "Thu",
  Friday: "Fri",
  Saturday: "Sat",
  Sunday: "Sun",
};

// ─── Helpers ──────────────────────────────────────────────────────────────────
function timeToMinutes(t: string) {
  const [h, m] = t.split(":").map(Number);
  return h * 60 + m;
}

function toHHMM(date: string) {
  return date.split("T")[1]?.slice(0, 5) ?? "";
}

function initials(name: string) {
  return name
    .split(" ")
    .slice(0, 2)
    .map((n) => n[0])
    .join("")
    .toUpperCase();
}

function formatDate(iso: string) {
  const date = new Date(iso);
  return date.toLocaleDateString("id-ID", {
    weekday: "long", // Menampilkan nama hari (contoh: Rabu)
    day: "numeric",
    month: "long",
    year: "numeric",
  });
}

// ─── Shared UI ────────────────────────────────────────────────────────────────
const AVATAR_COLORS = [
  "bg-indigo-500",
  "bg-violet-500",
  "bg-sky-500",
  "bg-emerald-500",
  "bg-amber-500",
  "bg-rose-500",
];
function Avatar({
  name,
  size = "md",
}: {
  name: string;
  size?: "sm" | "md" | "lg";
}) {
  const idx = name.charCodeAt(0) % AVATAR_COLORS.length;
  const sz =
    size === "sm"
      ? "w-7 h-7 text-xs"
      : size === "lg"
        ? "w-10 h-10 text-sm"
        : "w-8 h-8 text-xs";
  return (
    <div
      className={`${AVATAR_COLORS[idx]} ${sz} rounded-full flex items-center justify-center text-white font-semibold flex-shrink-0`}
    >
      {initials(name)}
    </div>
  );
}

function Badge({
  label,
  variant = "default",
}: {
  label: string;
  variant?: "default" | "success" | "warning" | "danger" | "info";
}) {
  const styles = {
    default: "bg-slate-100 text-slate-600",
    success: "bg-emerald-50 text-emerald-700 border border-emerald-200",
    warning: "bg-amber-50 text-amber-700 border border-amber-200",
    danger: "bg-red-50 text-red-600 border border-red-200",
    info: "bg-indigo-50 text-indigo-700 border border-indigo-200",
  };
  return (
    <span
      className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${styles[variant]}`}
    >
      {label}
    </span>
  );
}

function Button({
  children,
  onClick,
  variant = "primary",
  size = "md",
  disabled = false,
  icon,
  className = "",
  type = "button",
}: {
  children: React.ReactNode;
  onClick?: () => void;
  variant?: "primary" | "secondary" | "ghost" | "danger";
  size?: "sm" | "md";
  disabled?: boolean;
  icon?: React.ReactNode;
  className?: string;
  type?: "button" | "submit";
}) {
  const base =
    "inline-flex items-center gap-1.5 font-medium rounded-lg transition-all focus:outline-none focus:ring-2 focus:ring-offset-1 disabled:opacity-50 disabled:cursor-not-allowed";
  const sizes = { sm: "px-3 py-1.5 text-xs", md: "px-4 py-2 text-sm" };
  const variants = {
    primary:
      "bg-[#4361EE] text-white hover:bg-[#3451DB] focus:ring-[#4361EE]/50 shadow-sm",
    secondary:
      "bg-white text-slate-700 border border-slate-200 hover:bg-slate-50 focus:ring-slate-200 shadow-sm",
    ghost: "text-slate-600 hover:bg-slate-100 focus:ring-slate-200",
    danger:
      "bg-red-500 text-white hover:bg-red-600 focus:ring-red-300 shadow-sm",
  };
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`${base} ${sizes[size]} ${variants[variant]} ${className}`}
    >
      {icon}
      {children}
    </button>
  );
}

function Input({
  label,
  value,
  onChange,
  type = "text",
  placeholder,
  required,
  min,
  max,
}: {
  label?: string;
  value: string;
  onChange: (v: string) => void;
  type?: string;
  placeholder?: string;
  required?: boolean;
  min?: string;
  max?: string;
}) {
  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label className="text-xs font-medium text-slate-600">
          {label}
          {required && <span className="text-red-400 ml-0.5">*</span>}
        </label>
      )}
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        required={required}
        min={min}
        max={max}
        className="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg bg-white text-slate-800 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#4361EE]/30 focus:border-[#4361EE] transition-all"
      />
    </div>
  );
}

function Select({
  label,
  value,
  onChange,
  options,
  placeholder,
  required,
}: {
  label?: string;
  value: string;
  onChange: (v: string) => void;
  options: { value: string; label: string }[];
  placeholder?: string;
  required?: boolean;
}) {
  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label className="text-xs font-medium text-slate-600">
          {label}
          {required && <span className="text-red-400 ml-0.5">*</span>}
        </label>
      )}
      <div className="relative">
        <select
          value={value}
          onChange={(e) => onChange(e.target.value)}
          required={required}
          className="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg bg-white text-slate-800 appearance-none focus:outline-none focus:ring-2 focus:ring-[#4361EE]/30 focus:border-[#4361EE] transition-all pr-8"
        >
          {placeholder && <option value="">{placeholder}</option>}
          {options.map((o) => (
            <option key={o.value} value={o.value}>
              {o.label}
            </option>
          ))}
        </select>
        <ChevronDown
          size={14}
          className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none"
        />
      </div>
    </div>
  );
}

function Toast({
  message,
  type,
  onClose,
}: {
  message: string;
  type: "success" | "error";
  onClose: () => void;
}) {
  const styles =
    type === "success"
      ? "bg-emerald-50 border-emerald-200 text-emerald-800"
      : "bg-red-50 border-red-200 text-red-800";
  const Icon = type === "success" ? CheckCircle2 : AlertTriangle;
  const iconColor = type === "success" ? "text-emerald-500" : "text-red-500";
  return (
    <div
      className={`fixed top-5 right-5 z-50 flex items-start gap-3 px-4 py-3 border rounded-xl shadow-xl max-w-sm ${styles}`}
    >
      <Icon size={18} className={`flex-shrink-0 mt-0.5 ${iconColor}`} />
      <p className="text-sm font-medium leading-snug">{message}</p>
      <button
        onClick={onClose}
        className="ml-auto flex-shrink-0 hover:opacity-70"
      >
        <X size={14} />
      </button>
    </div>
  );
}

// ─── Sidebar ──────────────────────────────────────────────────────────────────
const NAV: Record<
  Role,
  { icon: React.ElementType; label: string; page: string }[]
> = {
  student: [
    { icon: LayoutDashboard, label: "Dashboard", page: "dashboard" },
    { icon: BookOpen, label: "Book a Lesson", page: "book" },
    { icon: Calendar, label: "My Lessons", page: "mylessons" },
  ],
  teacher: [
    { icon: LayoutDashboard, label: "Dashboard", page: "dashboard" },
    { icon: Clock, label: "Availability", page: "availability" },
    { icon: Calendar, label: "My Schedule", page: "schedule" },
  ],
  admin: [
    { icon: LayoutDashboard, label: "Dashboard", page: "dashboard" },
    { icon: GraduationCap, label: "Students", page: "students" },
    { icon: UserCheck, label: "Teachers", page: "teachers" },
    { icon: Shield, label: "Admins", page: "admins" },
    { icon: BookMarked, label: "Lessons", page: "lessons" },
    { icon: Calendar, label: "Manage Schedules", page: "schedules" },
  ],
};

function Sidebar({
  role,
  page,
  setPage,
}: {
  role: Role;
  page: string;
  setPage: (p: string) => void;
}) {
  const items = NAV[role];
  const roleLabel = {
    student: "Student Portal",
    teacher: "Teacher Portal",
    admin: "Admin Console",
  }[role];
  const roleColor = {
    student: "bg-sky-500",
    teacher: "bg-violet-500",
    admin: "bg-indigo-600",
  }[role];
  return (
    <aside className="w-56 flex-shrink-0 h-screen flex flex-col bg-[#1C2B3A] fixed left-0 top-0 z-20">
      <div className="px-5 py-5 border-b border-white/10">
        <div className="flex items-center gap-2.5">
          <div
            className={`w-8 h-8 rounded-lg ${roleColor} flex items-center justify-center flex-shrink-0`}
          >
            <Layers size={16} className="text-white" />
          </div>
          <div>
            <p className="text-white text-sm font-bold leading-tight">
              EduCAPY
            </p>
            <p className="text-white/40 text-xs leading-tight">{roleLabel}</p>
          </div>
        </div>
      </div>
      <nav className="flex-1 px-3 py-4 space-y-0.5 overflow-y-auto">
        {items.map(({ icon: Icon, label, page: p }) => (
          <button
            key={p}
            onClick={() => setPage(p)}
            className={`w-full flex items-center gap-2.5 px-3 py-2.5 rounded-lg text-sm font-medium transition-all text-left ${page === p
              ? "bg-white/10 text-white"
              : "text-white/50 hover:text-white hover:bg-white/5"
              }`}
          >
            <Icon size={16} className="flex-shrink-0" />
            {label}
          </button>
        ))}
      </nav>
      <div className="px-3 pb-4 border-t border-white/10 pt-3">
        <button className="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-lg text-sm text-white/40 hover:text-white/70 hover:bg-white/5 transition-all">
          <LogOut size={16} /> Sign Out
        </button>
      </div>
    </aside>
  );
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
function TopBar({
  role,
  setRole,
  globalSearch,
  setGlobalSearch,
  onLogout,
  loggedInName,
}: {
  role: Role;
  setRole: (r: Role) => void;
  globalSearch: string;
  setGlobalSearch: (s: string) => void;
  onLogout: () => void;
  loggedInName: string;
}) {
  const roleUser = {
    student: { name: loggedInName || "Rani Kusuma", badge: "Student" },
    teacher: { name: loggedInName || "Dr. Ayu Rahmawati", badge: "Teacher" },
    admin: { name: loggedInName || "Reza Firmansyah", badge: "Admin" },
  }[role];
  return (
    <header className="h-14 bg-white border-b border-slate-200 flex items-center px-6 gap-4 sticky top-0 z-10">
      <div className="flex items-center gap-2 flex-1 max-w-sm">
        <Search size={15} className="text-slate-400 flex-shrink-0" />
        <input
          value={globalSearch}
          onChange={(e) => setGlobalSearch(e.target.value)}
          placeholder="Search students, teachers, schedules..."
          className="w-full text-sm text-slate-700 placeholder:text-slate-400 bg-transparent outline-none"
        />
      </div>
      <div className="flex items-center gap-3 ml-auto">
        <div className="flex items-center gap-1 bg-slate-100 rounded-lg p-1">
          {(["student", "teacher", "admin"] as Role[]).map((r) => null)}
        </div>
        <button className="relative w-8 h-8 flex items-center justify-center rounded-lg hover:bg-slate-100 transition-all">
          <Bell size={16} className="text-slate-500" />
          <span className="absolute top-1.5 right-1.5 w-1.5 h-1.5 bg-[#4361EE] rounded-full" />
        </button>
        <div className="flex items-center gap-2 pl-2 border-l border-slate-200">
          <Avatar name={roleUser.name} size="sm" />
          <div className="hidden xl:block">
            <p className="text-xs font-semibold text-slate-700 leading-tight">
              {roleUser.name}
            </p>
            <p className="text-xs text-slate-400 leading-tight">
              {roleUser.badge}
            </p>
          </div>
          <button
            onClick={onLogout}
            title="Sign out"
            className="ml-1 p-1.5 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-all"
          >
            <LogOut size={14} />
          </button>
        </div>
      </div>
    </header>
  );
}

// ─── Page: Book a Lesson (Student) ───────────────────────────────────────────
function BookLesson({ loggedInId, setActiveLessons }: any) {
  const [step, setStep] = useState(1);
  const [selJenjang, setSelJenjang] = useState("");
  const [selMapel, setSelMapel] = useState("");
  const [selGurus, setSelGurus] = useState<number[]>([]);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [toast, setToast] = useState<{
    msg: string;
    type: "success" | "error";
  } | null>(null);
  const [booked, setBooked] = useState(false);

  const [selSlots, setSelSlots] = useState<JadwalKesediaan[]>([]);
  const [selPickedDays, setSelPickedDays] = useState<string[]>([]);

  const filteredGuru = useMemo(() => {
    if (!selJenjang || !selMapel) return [];
    const validGuruIds = KEAHLIAN.filter(
      (k) =>
        k.id_jenjang === Number(selJenjang) && k.id_mapel === Number(selMapel),
    ).map((k) => k.id_guru);
    return GURU.filter((g) => validGuruIds.includes(g.id));
  }, [selJenjang, selMapel]);

  const guruJadwal = useMemo(() => {
    if (selGurus.length === 0) return [];
    return JADWAL.filter(
      (j) => selGurus.includes(j.id_guru) && j.status === "tersedia",
    );
  }, [selGurus]);

  const availableDays = useMemo(
    () => [...new Set(guruJadwal.map((j) => j.hari))],
    [guruJadwal],
  );

  const slotsForPickedDays = useMemo(() => {
    return guruJadwal.filter((j) => selPickedDays.includes(j.hari));
  }, [selPickedDays, guruJadwal]);

  function togglePickedDay(day: string) {
    setSelPickedDays((prev) => {
      const next = prev.includes(day)
        ? prev.filter((d) => d !== day)
        : [...prev, day];
      setSelSlots((s) => s.filter((sl) => next.includes(sl.hari)));
      return next;
    });
  }

  function toggleSlot(slot: JadwalKesediaan) {
    setSelSlots((prev) => {
      const exists = prev.find((s) => s.id === slot.id);
      if (exists) return prev.filter((s) => s.id !== slot.id);
      return [...prev, slot];
    });
    if (selSlots.length === 0) setStep(4);
  }

  const selMapelObj = MAPEL.find((m) => m.id === Number(selMapel));
  const selJenjangObj = JENJANG.find((j) => j.id === Number(selJenjang));
  const selGuruObjs = GURU.filter((g) => selGurus.includes(g.id));

  const bookedSlotIds = useMemo(() => {
    if (selGurus.length === 0 || !startDate || !endDate)
      return new Set<number>();
    const bookedIds = new Set<number>();
    guruJadwal.forEach((j) => {
      const hasBooking = LES_DATA.some((l) => {
        if (l.id_jadwal !== j.id) return false;
        const d = l.tanggal_mulai.split("T")[0];
        return d >= startDate && d <= endDate;
      });
      if (hasBooking) bookedIds.add(j.id);
    });
    return bookedIds;
  }, [selGurus, startDate, endDate, guruJadwal]);

  async function handleBook() {
    if (selSlots.length === 0 || !startDate || !endDate) return;

    try {
      for (const slot of selSlots) {
        // Hitung durasi dalam menit berdasarkan slot yang dipilih
        const startMins = timeToMinutes(slot.jam_mulai);
        const endMins = timeToMinutes(slot.jam_selesai);
        const durasiMenit = endMins - startMins;

        const payload = {
          id_siswa: loggedInId,
          id_jadwal: slot.id,
          id_mapel: Number(selMapel),
          id_jenjang: Number(selJenjang),
          tanggal_mulai: `${startDate}T${slot.jam_mulai}`,
          tanggal_selesai: `${endDate}T${slot.jam_selesai}`,
          durasi: durasiMenit, // <-- Sekarang durasinya dikirim sesuai hitungan asli
        };

        const response = await fetch("http://localhost:8080/api/les", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        const result = await response.json();
        if (result.status !== "sukses") {
          setToast({ msg: "Gagal booking: " + result.pesan, type: "error" });
          return;
        }
      }

      setBooked(true);
      setToast({
        msg: "Recurring lessons booked successfully!",
        type: "success",
      });

      if (loggedInId) {
        fetch(`http://localhost:8080/api/les/siswa?id_siswa=${loggedInId}`)
          .then((res) => res.json())
          .then((data) => setActiveLessons(data));
      }
    } catch {
      setToast({
        msg: "Server tidak merespon. Pastikan Backend Java berjalan.",
        type: "error",
      });
    }
  }

  const summaryComplete =
    selJenjang &&
    selMapel &&
    selGurus.length > 0 &&
    selSlots.length > 0 &&
    startDate &&
    endDate;
  const totalWeeks =
    startDate && endDate
      ? Math.ceil(
        (new Date(endDate).getTime() - new Date(startDate).getTime()) /
        (7 * 86400000),
      )
      : 0;
  const totalSessions = totalWeeks * selSlots.length;

  return (
    <div className="flex gap-0 h-full relative">
      {toast && (
        <Toast
          message={toast.msg}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
      <div className="flex-[3] pr-6 space-y-6 overflow-y-auto max-h-[calc(100vh-3.5rem)] pb-8">
        <div>
          <h1 className="text-xl font-bold text-slate-800">Book a Lesson</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Follow the steps below to schedule your private tutoring session.
          </p>
        </div>
        <div className="flex items-center gap-0">
          {[
            "Education Level & Subject",
            "Choose Teacher",
            "Availability",
            "Schedule Details",
          ].map((s, i) => (
            <div key={s} className="flex items-center flex-1 last:flex-none">
              <button
                onClick={() => setStep(i + 1)}
                className="flex items-center gap-2 group"
              >
                <div
                  className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 transition-all ${step > i + 1 ? "bg-emerald-500 text-white" : step === i + 1 ? "bg-[#4361EE] text-white" : "bg-slate-200 text-slate-500"}`}
                >
                  {step > i + 1 ? <Check size={12} /> : i + 1}
                </div>
                <span
                  className={`text-xs font-medium whitespace-nowrap ${step === i + 1 ? "text-[#4361EE]" : "text-slate-400"}`}
                >
                  {s}
                </span>
              </button>
              {i < 3 && (
                <div
                  className={`flex-1 h-px mx-3 ${step > i + 1 ? "bg-emerald-400" : "bg-slate-200"}`}
                />
              )}
            </div>
          ))}
        </div>

        {/* Step 1 */}
        <div className="bg-white border border-slate-200 rounded-xl p-6 space-y-4">
          <h2 className="text-sm font-semibold text-slate-700 flex items-center gap-2">
            <span className="w-5 h-5 bg-[#4361EE] text-white rounded-full flex items-center justify-center text-xs">
              1
            </span>{" "}
            Education Level &amp; Subject
          </h2>
          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Education Level"
              value={selJenjang}
              onChange={(v: string) => {
                setSelJenjang(v);
                setSelMapel("");
                setSelGurus([]);
                setSelSlots([]);
              }}
              options={JENJANG.map((j) => ({
                value: String(j.id),
                label: j.nama,
              }))}
              placeholder="Select level"
              required
            />
            <Select
              label="Subject"
              value={selMapel}
              onChange={(v: string) => {
                setSelMapel(v);
                setSelGurus([]);
                setSelSlots([]);
              }}
              options={MAPEL.map((m) => ({
                value: String(m.id),
                label: m.nama,
              }))}
              placeholder={selJenjang ? "Select subject" : "Select level first"}
              required
            />
          </div>
          {selJenjang && selMapel && (
            <div className="flex justify-end">
              <Button onClick={() => setStep(2)} size="sm">
                Next: Choose Teacher <ChevronRight size={14} />
              </Button>
            </div>
          )}
        </div>

        {/* Step 2 */}
        {selMapel && (
          <div className="bg-white border border-slate-200 rounded-xl p-6 space-y-4">
            <h2 className="text-sm font-semibold text-slate-700 flex items-center gap-2">
              <span className="w-5 h-5 bg-[#4361EE] text-white rounded-full flex items-center justify-center text-xs">
                2
              </span>{" "}
              Choose Teachers
            </h2>
            {filteredGuru.length === 0 ? (
              <p className="text-sm text-slate-400 text-center py-4">
                No teachers available.
              </p>
            ) : (
              <div className="grid grid-cols-2 gap-3">
                {filteredGuru.map((g) => (
                  <button
                    key={g.id}
                    onClick={() => {
                      setSelGurus((prev) =>
                        prev.includes(g.id)
                          ? prev.filter((id) => id !== g.id)
                          : [...prev, g.id],
                      );
                      setSelPickedDays([]);
                      setSelSlots([]);
                    }}
                    className={`text-left p-4 border rounded-xl flex items-start gap-3 transition-all ${selGurus.includes(g.id) ? "border-[#4361EE] bg-indigo-50 ring-1 ring-[#4361EE]/20" : "border-slate-200 hover:bg-slate-50"}`}
                  >
                    <Avatar name={g.nama} size="lg" />
                    <div>
                      <p className="text-sm font-semibold text-slate-800 leading-tight">
                        {g.nama}
                      </p>
                      <p className="text-xs text-slate-400 mt-0.5">{g.email}</p>
                    </div>
                    {selGurus.includes(g.id) && (
                      <Check size={16} className="ml-auto text-[#4361EE]" />
                    )}
                  </button>
                ))}
              </div>
            )}
            {selGurus.length > 0 && (
              <div className="flex justify-end pt-2">
                <Button onClick={() => setStep(3)} size="sm">
                  Next: Availability <ChevronRight size={14} />
                </Button>
              </div>
            )}
          </div>
        )}

        {/* Step 3 */}
        {selGurus.length > 0 && (
          <div className="bg-white border border-slate-200 rounded-xl p-6 space-y-5">
            <h2 className="text-sm font-semibold text-slate-700 flex items-center gap-2">
              <span className="w-5 h-5 bg-[#4361EE] text-white rounded-full flex items-center justify-center text-xs">
                3
              </span>{" "}
              Teacher Availability
            </h2>
            <div className="space-y-2">
              <p className="text-xs font-medium text-slate-600">
                Pilih Hari <span className="text-red-400">*</span>
              </p>
              <div className="flex gap-2 flex-wrap">
                {HARI.map((day) => {
                  const isAvail = availableDays.includes(day);
                  const sel = selPickedDays.includes(day);
                  return (
                    <button
                      key={day}
                      type="button"
                      onClick={() => isAvail && togglePickedDay(day)}
                      disabled={!isAvail}
                      className={`px-3 py-1.5 rounded-full text-xs font-semibold border transition-all ${!isAvail ? "border-slate-200 text-slate-300 bg-slate-50 cursor-not-allowed" : sel ? "border-[#4361EE] bg-[#4361EE] text-white" : "border-slate-300 text-slate-600 hover:border-[#4361EE] hover:text-[#4361EE]"}`}
                    >
                      {DAY_SHORT[day]}
                    </button>
                  );
                })}
              </div>
            </div>
            {selPickedDays.length > 0 && (
              <div className="space-y-4">
                <p className="text-xs font-medium text-slate-600">
                  Pilih Slot (1 jam) <span className="text-red-400">*</span>
                </p>
                {selPickedDays.map((day) => {
                  const daySlots = slotsForPickedDays.filter(
                    (s) => s.hari === day,
                  );
                  return (
                    <div key={day} className="space-y-2">
                      <p className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                        {day}
                      </p>
                      <div className="flex gap-2 flex-wrap">
                        {daySlots.map((slot) => {
                          const isSel = selSlots.some((s) => s.id === slot.id);
                          const isBooked = bookedSlotIds.has(slot.id);
                          const slotGuru = GURU.find(
                            (g) => g.id === slot.id_guru,
                          );
                          return (
                            <button
                              key={slot.id}
                              type="button"
                              onClick={() => !isBooked && toggleSlot(slot)}
                              disabled={isBooked}
                              className={`flex items-center gap-1.5 px-3 py-2 rounded-lg border text-xs font-semibold transition-all ${isBooked ? "bg-slate-50 border-slate-200 text-slate-400 cursor-not-allowed" : isSel ? "bg-[#4361EE] border-[#4361EE] text-white" : "bg-white border-slate-200 text-slate-600 hover:border-[#4361EE] hover:text-[#4361EE]"}`}
                            >
                              <Clock size={11} />
                              {slot.jam_mulai} – {slot.jam_selesai} (
                              {slotGuru?.nama.split(" ")[0]})
                              {isBooked && (
                                <span className="ml-1 text-red-400">
                                  (Full)
                                </span>
                              )}
                              {isSel && <Check size={11} className="ml-1" />}
                            </button>
                          );
                        })}
                      </div>
                    </div>
                  );
                })}
                {selSlots.length > 0 && (
                  <div className="flex justify-end">
                    <Button onClick={() => setStep(4)} size="sm">
                      Next: Schedule Details <ChevronRight size={14} />
                    </Button>
                  </div>
                )}
              </div>
            )}
          </div>
        )}

        {/* Step 4 */}
        {selSlots.length > 0 && (
          <div className="bg-white border border-slate-200 rounded-xl p-6 space-y-5">
            <h2 className="text-sm font-semibold text-slate-700 flex items-center gap-2">
              <span className="w-5 h-5 bg-[#4361EE] text-white rounded-full flex items-center justify-center text-xs">
                4
              </span>{" "}
              Schedule Details
            </h2>
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Start Date"
                type="date"
                value={startDate}
                onChange={setStartDate}
                required
              />
              <Input
                label="End Date"
                type="date"
                value={endDate}
                onChange={setEndDate}
                min={startDate}
                required
              />
            </div>
            {summaryComplete && (
              <Button
                onClick={handleBook}
                disabled={booked}
                icon={<BookOpen size={15} />}
              >
                {booked ? "Booked!" : "Confirm Recurring Booking"}
              </Button>
            )}
          </div>
        )}
      </div>

      {/* Right — Summary */}
      <div className="flex-[2] pl-6 border-l border-slate-200">
        <div className="sticky top-6 space-y-4">
          <h2 className="text-sm font-semibold text-slate-700">
            Booking Summary
          </h2>
          <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
            <div className="bg-[#4361EE] px-5 py-4">
              <p className="text-white/70 text-xs font-medium uppercase tracking-wider">
                Recurring Tutoring Session
              </p>
              <p className="text-white text-lg font-bold mt-1">
                {selMapelObj ? selMapelObj.nama : "—"}{" "}
                {selJenjangObj && (
                  <span className="text-white/70 text-sm font-normal ml-1">
                    ({selJenjangObj.nama})
                  </span>
                )}
              </p>
            </div>
            <div className="p-5 space-y-4">
              <SummaryRow
                icon={<UserCheck size={14} />}
                label="Teacher"
                value={
                  selGuruObjs.length > 0
                    ? selGuruObjs.map((g) => g.nama).join(", ")
                    : "Not selected"
                }
              />
              <SummaryRow
                icon={<Clock size={14} />}
                label="Time Window"
                value={
                  selSlots.length > 0
                    ? `${selSlots.length} slot dipilih`
                    : "Not selected"
                }
              />
              <div className="border-t border-slate-100 pt-4 space-y-3">
                <SummaryRow
                  icon={<Calendar size={14} />}
                  label="Start Date"
                  value={
                    startDate ? formatDate(startDate + "T00:00") : "Not set"
                  }
                />
                <SummaryRow
                  icon={<Calendar size={14} />}
                  label="End Date"
                  value={endDate ? formatDate(endDate + "T00:00") : "Not set"}
                />
                <SummaryRow
                  icon={<Clock size={14} />}
                  label="Slots"
                  value={
                    selSlots.length > 0
                      ? selSlots
                        .map((s) => `${s.hari} ${s.jam_mulai}`)
                        .join(", ")
                      : "Not selected"
                  }
                />
              </div>
              {summaryComplete && (
                <div
                  className={`rounded-lg p-3 text-xs font-medium flex items-center gap-2 ${booked ? "bg-emerald-50 text-emerald-700" : "bg-indigo-50 text-[#4361EE]"}`}
                >
                  {booked ? (
                    <CheckCircle2 size={14} />
                  ) : (
                    <AlertTriangle size={14} />
                  )}
                  {booked
                    ? "Your lessons have been confirmed!"
                    : "Ready to book. Review and confirm."}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function SummaryRow({ icon, label, value }: any) {
  return (
    <div className="flex items-center gap-3">
      <div className="text-slate-400 flex-shrink-0">{icon}</div>
      <div className="flex-1 min-w-0">
        <p className="text-xs text-slate-400 leading-tight">{label}</p>
        <p className="text-sm font-medium text-slate-700 truncate">{value}</p>
      </div>
    </div>
  );
}

// ─── Page: My Lessons (Student) ────────────────────────────────────────────
function MyLessons({ loggedInId, activeLessons }: any) {
  const myLes = activeLessons;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-800">My Lessons</h1>
      </div>
      <div className="flex gap-6 items-start">
        <div className="flex-1 bg-white border border-slate-200 rounded-xl overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-100">
                {/* Kolom Status sudah bersih, tinggal 5 kolom ini */}
                {["Subject", "Teacher", "Date", "Time", "Duration"].map((h) => (
                  <th
                    key={h}
                    className="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider"
                  >
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {Array.isArray(myLes) &&
                myLes.map((les: any) => {
                  const mapel = MAPEL.find((m) => m.id === les.id_mapel);
                  const jadwal = JADWAL.find((j) => j.id === les.id_jadwal);
                  const guru = GURU.find((g) => g.id === jadwal?.id_guru);

                  // 1. Ambil Nama Hari (cukup ambil dari data jadwal)
                  const dayName = jadwal ? jadwal.hari : "-";

                  // 2. Format Jam
                  const start = (les.jam_mulai || jadwal?.jam_mulai || "00:00").substring(0, 5);
                  const end = (les.jam_selesai || jadwal?.jam_selesai || "00:00").substring(0, 5);

                  // 3. Durasi
                  const startMins = timeToMinutes(start);
                  const endMins = timeToMinutes(end);
                  const dur = startMins > 0 ? ((endMins - startMins) / 60).toFixed(1) : "0.0";

                  return (
                    <tr key={les.id} className="border-b border-slate-50 hover:bg-slate-50/80 transition-colors">
                      <td className="px-5 py-3.5 font-medium text-slate-800">{mapel?.nama || "Unknown"}</td>
                      <td className="px-5 py-3.5">
                        <div className="flex items-center gap-2">
                          <Avatar name={guru?.nama ?? "?"} size="sm" />
                          <span className="text-slate-600">{guru?.nama || "-"}</span>
                        </div>
                      </td>
                      {/* Tampilan Hari saja */}
                      <td className="px-5 py-3.5 text-slate-600 font-medium">
                        {dayName}
                      </td>
                      <td className="px-5 py-3.5 text-slate-600">{start} - {end}</td>
                      <td className="px-5 py-3.5 text-slate-600">{dur} hrs</td>
                    </tr>
                  );
                })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

// ─── Page: Teacher Availability ────────────────────────────────────────────
function TeacherAvailability({ loggedinId }: any) {
  const guruId = loggedinId;
  const [availList, setAvailList] = useState<JadwalKesediaan[]>(
    JADWAL.filter((j) => j.id_guru === guruId),
  );
  const [hari, setHari] = useState("");
  const [jamMulai, setJamMulai] = useState("");
  const [jamSelesai, setJamSelesai] = useState("");
  const [toast, setToast] = useState<string | null>(null);

  function handleAdd() {
    if (!hari || !jamMulai || !jamSelesai) return;

    let start = timeToMinutes(jamMulai);
    const end = timeToMinutes(jamSelesai);
    const newSlots: JadwalKesediaan[] = [];

    while (start + 60 <= end) {
      const slotStart = `${String(Math.floor(start / 60)).padStart(2, "0")}:${String(start % 60).padStart(2, "0")}`;
      const slotEnd = `${String(Math.floor((start + 60) / 60)).padStart(2, "0")}:${String((start + 60) % 60).padStart(2, "0")}`;

      newSlots.push({
        id: Date.now() + start,
        hari,
        jam_mulai: slotStart,
        jam_selesai: slotEnd,
        id_guru: guruId,
        id_admin: 1,
        status: "tersedia",
      });
      start += 60;
    }

    setAvailList((p) => [...p, ...newSlots]);
    setToast("Availability slots added successfully!");
    setHari("");
    setJamMulai("");
    setJamSelesai("");
  }

  function handleRemove(id: number) {
    setAvailList((p) => p.filter((j) => j.id !== id));
  }

  return (
    <div className="space-y-6">
      {toast && (
        <Toast message={toast} type="success" onClose={() => setToast(null)} />
      )}
      <div>
        <h1 className="text-xl font-bold text-slate-800">
          Manage Availability
        </h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Set the days and time windows when you are available to teach.
        </p>
      </div>
      <div className="flex gap-6 items-start">
        <div className="w-80 flex-shrink-0 bg-white border border-slate-200 rounded-xl p-5 space-y-4">
          <h2 className="text-sm font-semibold text-slate-700">
            Add Open Slots
          </h2>
          <Select
            label="Day"
            value={hari}
            onChange={setHari}
            options={HARI.map((h) => ({ value: h, label: h }))}
            placeholder="Select day"
            required
          />
          <div className="grid grid-cols-2 gap-3">
            <Input
              label="Start Time"
              type="time"
              value={jamMulai}
              onChange={setJamMulai}
              required
            />
            <Input
              label="End Time"
              type="time"
              value={jamSelesai}
              onChange={setJamSelesai}
              required
            />
          </div>
          <Button
            onClick={handleAdd}
            icon={<Plus size={15} />}
            className="w-full justify-center"
          >
            Add Time Slots
          </Button>
        </div>
        <div className="flex-1 bg-white border border-slate-200 rounded-xl overflow-hidden">
          <div className="px-5 py-3 border-b border-slate-100">
            <p className="text-sm font-semibold text-slate-700">
              Current Open Slots
            </p>
          </div>
          <div className="divide-y divide-slate-50">
            {availList.map((j) => (
              <div
                key={j.id}
                className="px-5 py-4 flex items-center justify-between hover:bg-slate-50/60 transition-colors"
              >
                <div className="flex items-center gap-4">
                  <div className="w-10 h-10 bg-indigo-50 rounded-lg flex items-center justify-center flex-shrink-0">
                    <Clock size={16} className="text-[#4361EE]" />
                  </div>
                  <div>
                    <p className="text-sm font-semibold text-slate-700">
                      {j.hari}
                    </p>
                    <p className="text-xs text-slate-400">
                      {j.jam_mulai} – {j.jam_selesai}
                    </p>
                  </div>
                  <Badge
                    label={j.status}
                    variant={j.status === "tersedia" ? "success" : "warning"}
                  />
                </div>
                <button
                  onClick={() => handleRemove(j.id)}
                  className="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

// ─── Page: Teacher Schedule (Calendar) ─────────────────────────────────────
function TeacherSchedule({ loggedInId }: any) {
  const guruId = loggedInId;
  const [selLesson, setSelLesson] = useState<Les | null>(null);

  // FIX: filter jadwal langsung by id_guru, tidak pakai id_keahlian
  const guruJadwalIds = JADWAL.filter((j) => j.id_guru === guruId).map(
    (j) => j.id,
  );
  const myLessons = LES_DATA.filter((l) => guruJadwalIds.includes(l.id_jadwal));

  const COLORS = [
    "bg-indigo-400",
    "bg-violet-400",
    "bg-sky-400",
    "bg-emerald-400",
    "bg-amber-400",
  ];

  const weekDates = HARI.map((h, i) => {
    const d = new Date(2025, 0, 20 + i);
    return { hari: h, date: d, iso: d.toISOString().split("T")[0] };
  });

  function getLessonsForDay(dayIso: string) {
    return myLessons.filter((l) => l.tanggal_mulai.split("T")[0] === dayIso);
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-800">My Schedule</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Weekly overview of your confirmed lesson bookings.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant="secondary"
            size="sm"
            icon={<ChevronLeft size={14} />}
          >
            Prev
          </Button>
          <span className="text-sm font-medium text-slate-700 px-2">
            Week of Jan 20, 2025
          </span>
          <Button variant="secondary" size="sm">
            Next <ChevronRight size={14} />
          </Button>
        </div>
      </div>
      <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
        <div className="grid grid-cols-7 divide-x divide-slate-100 border-b border-slate-200">
          {weekDates.map(({ hari, date }) => (
            <div key={hari} className="px-3 py-3 text-center">
              <p className="text-xs text-slate-400 font-medium">
                {hari.slice(0, 3).toUpperCase()}
              </p>
              <p className="text-base font-bold text-slate-700 mt-0.5">
                {date.getDate()}
              </p>
            </div>
          ))}
        </div>
        <div className="grid grid-cols-7 divide-x divide-slate-100 min-h-[280px]">
          {weekDates.map(({ hari, iso }, ci) => {
            const dayLessons = getLessonsForDay(iso);
            return (
              <div key={hari} className="p-2 space-y-1.5 min-h-[180px]">
                {dayLessons.length === 0 && (
                  <div className="text-xs text-slate-300 text-center mt-6">
                    —
                  </div>
                )}
                {dayLessons.map((les) => {
                  // FIX: mapel & siswa diambil dari les.id_mapel langsung
                  const mapel = MAPEL.find((m) => m.id === les.id_mapel);
                  const siswa = SISWA.find((s) => s.id === les.id_siswa);
                  const start = toHHMM(les.tanggal_mulai);
                  const end = toHHMM(les.tanggal_selesai);
                  return (
                    <button
                      key={les.id}
                      onClick={() => setSelLesson(les)}
                      className={`w-full text-left rounded-lg px-2.5 py-2 text-white ${COLORS[ci % COLORS.length]} hover:opacity-90 transition-opacity`}
                    >
                      <p className="text-xs font-bold">
                        {start} – {end}
                      </p>
                      <p className="text-xs opacity-75 truncate mt-0.5">
                        {siswa?.nama}
                      </p>
                    </button>
                  );
                })}
              </div>
            );
          })}
        </div>
      </div>

      {/* Lesson modal */}
      {selLesson &&
        (() => {
          // FIX: ambil data dari les.id_mapel, les.id_jenjang, jadwal.id_guru
          const mapel = MAPEL.find((m) => m.id === selLesson.id_mapel);
          const jenjang = JENJANG.find((j) => j.id === selLesson.id_jenjang);
          const jadwal = JADWAL.find((j) => j.id === selLesson.id_jadwal);
          const guru = GURU.find((g) => g.id === jadwal?.id_guru);
          const siswa = SISWA.find((s) => s.id === selLesson.id_siswa);
          const start = toHHMM(selLesson.tanggal_mulai);
          const end = toHHMM(selLesson.tanggal_selesai);
          return (
            <div
              className="fixed inset-0 z-40 bg-black/30 flex items-center justify-center p-6"
              onClick={() => setSelLesson(null)}
            >
              <div
                className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6 space-y-5"
                onClick={(e) => e.stopPropagation()}
              >
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-xs text-slate-400 uppercase tracking-wider font-medium">
                      Lesson Detail
                    </p>
                    <h3 className="text-lg font-bold text-slate-800 mt-0.5">
                      {mapel?.nama}{" "}
                      <span className="text-slate-400 font-normal text-base">
                        ({jenjang?.nama})
                      </span>
                    </h3>
                  </div>
                  <button
                    onClick={() => setSelLesson(null)}
                    className="p-1.5 hover:bg-slate-100 rounded-lg"
                  >
                    <X size={16} />
                  </button>
                </div>
                <div className="bg-slate-50 rounded-xl p-4 space-y-3">
                  <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    Student Info
                  </p>
                  <div className="flex items-center gap-3">
                    <Avatar name={siswa?.nama ?? "?"} size="lg" />
                    <div>
                      <p className="text-sm font-semibold text-slate-800">
                        {siswa?.nama}
                      </p>
                      <p className="text-xs text-slate-400">{siswa?.email}</p>
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-3 pt-1">
                    <div>
                      <p className="text-xs text-slate-400">Phone</p>
                      <p className="text-sm font-medium text-slate-700">
                        {siswa?.no_hp}
                      </p>
                    </div>
                    <div>
                      <p className="text-xs text-slate-400">Gender</p>
                      <p className="text-sm font-medium text-slate-700">
                        {siswa?.jenis_kelamin === "L" ? "Male" : "Female"}
                      </p>
                    </div>
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div className="bg-indigo-50 rounded-xl p-3">
                    <p className="text-xs text-slate-500">Date</p>
                    <p className="text-sm font-semibold text-slate-700 mt-0.5">
                      {formatDate(selLesson.tanggal_mulai)}
                    </p>
                  </div>
                  <div className="bg-indigo-50 rounded-xl p-3">
                    <p className="text-xs text-slate-500">Time</p>
                    <p className="text-sm font-semibold text-slate-700 mt-0.5">
                      {start} – {end}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          );
        })()}
    </div>
  );
}

// ─── Admin: Generic Data Table ────────────────────────────────────────────
type DrawerMode = "add" | "edit" | null;

function AdminStudents({ search }: { search: string }) {
  const [drawer, setDrawer] = useState<DrawerMode>(null);
  const [editItem, setEditItem] = useState<Siswa | null>(null);
  const [students, setStudents] = useState<Siswa[]>(SISWA);
  const [sortField, setSortField] = useState<keyof Siswa>("nama");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("asc");
  const [form, setForm] = useState<Partial<Siswa>>({});

  const filtered = students
    .filter((s) =>
      [s.nama, s.email, s.no_hp]
        .join(" ")
        .toLowerCase()
        .includes(search.toLowerCase()),
    )
    .sort((a, b) => {
      const av = String(a[sortField] ?? "");
      const bv = String(b[sortField] ?? "");
      return sortDir === "asc" ? av.localeCompare(bv) : bv.localeCompare(av);
    });

  function openAdd() {
    setForm({});
    setEditItem(null);
    setDrawer("add");
  }
  function openEdit(s: Siswa) {
    setForm(s);
    setEditItem(s);
    setDrawer("edit");
  }
  function handleDelete(id: number) {
    setStudents((p) => p.filter((s) => s.id !== id));
  }
  function handleSave() {
    if (drawer === "add") {
      const newId = Math.floor(Math.random() * 10000);
      const dataSiswaBaru = {
        id_siswa: newId,
        id_jenjang: Number(form.id_jenjang),
        nama: form.nama,
        tanggal_lahir: form.tanggal_lahir,
        jenis_Kelamin: form.jenis_kelamin,
        no_hp: form.no_hp,
        email: form.email,
        pswrd: "siswa123",
      };

      fetch("http://localhost:8080/api/siswa", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dataSiswaBaru),
      })
        .then((response) => response.json())
        .then((data) => {
          if (data.status === "sukses") {
            setStudents((p) => [...p, { ...form, id: newId } as Siswa]);
            alert("Sukses! Data " + form.nama + " tersimpan di Azure SQL.");
          } else {
            alert("Gagal menyimpan ke database: " + data.pesan);
          }
        })
        .catch((error) => {
          console.error(error);
          alert(
            "Gagal koneksi ke Back-End. Pastikan BackendServer.java menyala!",
          );
        });
    } else if (editItem) {
      setStudents((p) =>
        p.map((s) => (s.id === editItem.id ? ({ ...s, ...form } as Siswa) : s)),
      );
    }
    setDrawer(null);
  }
  function toggleSort(f: keyof Siswa) {
    if (sortField === f) setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    else {
      setSortField(f);
      setSortDir("asc");
    }
  }

  const jenjangMap: Record<number, string> = { 1: "SD", 2: "SMP", 3: "SMA" };

  return (
    <div className="space-y-5 relative">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-800">Students</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Manage all registered students in the system.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="secondary" size="sm" icon={<Download size={14} />}>
            Export
          </Button>
          <Button onClick={openAdd} icon={<Plus size={15} />}>
            Add Student
          </Button>
        </div>
      </div>
      <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
        <div className="px-5 py-3 border-b border-slate-100 flex items-center justify-between">
          <p className="text-sm text-slate-500">
            {filtered.length} of {students.length} students
          </p>
          <Button
            variant="ghost"
            size="sm"
            icon={<SlidersHorizontal size={14} />}
          >
            Filter
          </Button>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-slate-50 border-b border-slate-100">
              {[
                ["nama", "Name"],
                ["id_jenjang", "Level"],
                ["jenis_kelamin", "Gender"],
                ["tanggal_lahir", "Date of Birth"],
                ["email", "Email"],
                ["no_hp", "Phone"],
              ].map(([f, h]) => (
                <th key={f} className="px-5 py-3 text-left">
                  <button
                    onClick={() => toggleSort(f as keyof Siswa)}
                    className="flex items-center gap-1 text-xs font-semibold text-slate-500 uppercase tracking-wider hover:text-slate-700 transition-colors"
                  >
                    {h}
                    <ArrowUpDown size={11} className="text-slate-400" />
                  </button>
                </th>
              ))}
              <th className="px-5 py-3" />
            </tr>
          </thead>
          <tbody>
            {filtered.map((s) => (
              <tr
                key={s.id}
                className="border-b border-slate-50 hover:bg-slate-50/80 transition-colors group"
              >
                <td className="px-5 py-3.5">
                  <div className="flex items-center gap-2.5">
                    <Avatar name={s.nama} size="sm" />
                    <span className="font-medium text-slate-800">{s.nama}</span>
                  </div>
                </td>
                <td className="px-5 py-3.5">
                  <Badge
                    label={jenjangMap[s.id_jenjang] ?? "—"}
                    variant="info"
                  />
                </td>
                <td className="px-5 py-3.5 text-slate-600">
                  {s.jenis_kelamin === "L" ? "Male" : "Female"}
                </td>
                <td className="px-5 py-3.5 text-slate-500">
                  {s.tanggal_lahir}
                </td>
                <td className="px-5 py-3.5 text-slate-600">{s.email}</td>
                <td className="px-5 py-3.5 text-slate-600">{s.no_hp}</td>
                <td className="px-5 py-3.5">
                  <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                    <button
                      onClick={() => openEdit(s)}
                      className="p-1.5 text-slate-400 hover:text-[#4361EE] hover:bg-indigo-50 rounded-lg transition-all"
                    >
                      <Edit2 size={13} />
                    </button>
                    <button
                      onClick={() => handleDelete(s.id)}
                      className="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {drawer && (
        <>
          <div
            className="fixed inset-0 bg-black/25 z-30"
            onClick={() => setDrawer(null)}
          />
          <div className="fixed top-0 right-0 h-full w-96 bg-white shadow-2xl z-40 flex flex-col">
            <div className="px-6 py-5 border-b border-slate-200 flex items-center justify-between">
              <h2 className="text-base font-bold text-slate-800">
                {drawer === "add" ? "Add New Student" : "Edit Student"}
              </h2>
              <button
                onClick={() => setDrawer(null)}
                className="p-1.5 hover:bg-slate-100 rounded-lg"
              >
                <X size={16} />
              </button>
            </div>
            <div className="flex-1 overflow-y-auto px-6 py-5 space-y-4">
              <Input
                label="Full Name"
                value={form.nama ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, nama: v }))}
                required
              />
              <Select
                label="Jenjang"
                value={String(form.id_jenjang ?? "")}
                onChange={(v) =>
                  setForm((p) => ({ ...p, id_jenjang: Number(v) }))
                }
                options={JENJANG.map((j) => ({
                  value: String(j.id),
                  label: j.nama,
                }))}
                placeholder="Select jenjang"
                required
              />
              <Select
                label="Jenis Kelamin"
                value={form.jenis_kelamin ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, jenis_kelamin: v }))}
                options={[
                  { value: "L", label: "Male" },
                  { value: "P", label: "Female" },
                ]}
                placeholder="Select gender"
                required
              />
              <Input
                label="Date of Birth"
                type="date"
                value={form.tanggal_lahir ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, tanggal_lahir: v }))}
                required
              />
              <Input
                label="Email"
                type="email"
                value={form.email ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, email: v }))}
                required
              />
              <Input
                label="Phone Number"
                value={form.no_hp ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, no_hp: v }))}
                required
              />
            </div>
            <div className="px-6 py-4 border-t border-slate-200 flex gap-3">
              <Button
                variant="secondary"
                onClick={() => setDrawer(null)}
                className="flex-1 justify-center"
              >
                Cancel
              </Button>
              <Button onClick={handleSave} className="flex-1 justify-center">
                {drawer === "add" ? "Add Student" : "Save Changes"}
              </Button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

function AdminTeachers({ search }: { search: string }) {
  const [drawer, setDrawer] = useState<DrawerMode>(null);
  const [editItem, setEditItem] = useState<Guru | null>(null);
  const [teachers, setTeachers] = useState<Guru[]>(GURU);
  const [form, setForm] = useState<Partial<Guru>>({});
  const [formSubject, setFormSubject] = useState("");
  const [formEduLevel, setFormEduLevel] = useState("");

  const filtered = teachers.filter((t) =>
    [t.nama, t.email, t.no_hp]
      .join(" ")
      .toLowerCase()
      .includes(search.toLowerCase()),
  );

  function openAdd() {
    setForm({});
    setFormSubject("");
    setFormEduLevel("");
    setEditItem(null);
    setDrawer("add");
  }
  function openEdit(g: Guru) {
    setForm(g);
    setFormSubject("");
    setFormEduLevel("");
    setEditItem(g);
    setDrawer("edit");
  }
  function handleDelete(id: number) {
    setTeachers((p) => p.filter((g) => g.id !== id));
  }
  function handleSave() {
    if (drawer === "add")
      setTeachers((p) => [...p, { ...form, id: Date.now() } as Guru]);
    else if (editItem)
      setTeachers((p) =>
        p.map((g) => (g.id === editItem.id ? ({ ...g, ...form } as Guru) : g)),
      );
    setDrawer(null);
  }

  return (
    <div className="space-y-5 relative">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-800">Teachers</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Manage all registered tutors and their expertise.
          </p>
        </div>
        <Button onClick={openAdd} icon={<Plus size={15} />}>
          Add Teacher
        </Button>
      </div>
      <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
        <div className="px-5 py-3 border-b border-slate-100">
          <p className="text-sm text-slate-500">{filtered.length} teachers</p>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-slate-50 border-b border-slate-100">
              {["Name", "Email", "Phone", "Expertise", "Actions"].map((h) => (
                <th
                  key={h}
                  className="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider"
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.map((g) => {
              const expertise = KEAHLIAN.filter((k) => k.id_guru === g.id)
                .slice(0, 2)
                .map((k) => {
                  const mp = MAPEL.find((m) => m.id === k.id_mapel);
                  const jj = JENJANG.find((j) => j.id === k.id_jenjang);
                  return `${mp?.nama} ${jj?.nama}`;
                });
              return (
                <tr
                  key={g.id}
                  className="border-b border-slate-50 hover:bg-slate-50/80 transition-colors group"
                >
                  <td className="px-5 py-3.5">
                    <div className="flex items-center gap-2.5">
                      <Avatar name={g.nama} size="sm" />
                      <span className="font-medium text-slate-800">
                        {g.nama}
                      </span>
                    </div>
                  </td>
                  <td className="px-5 py-3.5 text-slate-600">{g.email}</td>
                  <td className="px-5 py-3.5 text-slate-600">{g.no_hp}</td>
                  <td className="px-5 py-3.5">
                    <div className="flex gap-1 flex-wrap">
                      {expertise.map((e) => (
                        <Badge key={e} label={e} variant="info" />
                      ))}
                    </div>
                  </td>
                  <td className="px-5 py-3.5">
                    <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button
                        onClick={() => openEdit(g)}
                        className="p-1.5 text-slate-400 hover:text-[#4361EE] hover:bg-indigo-50 rounded-lg transition-all"
                      >
                        <Edit2 size={13} />
                      </button>
                      <button
                        onClick={() => handleDelete(g.id)}
                        className="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all"
                      >
                        <Trash2 size={13} />
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
      {drawer && (
        <>
          <div
            className="fixed inset-0 bg-black/25 z-30"
            onClick={() => setDrawer(null)}
          />
          <div className="fixed top-0 right-0 h-full w-96 bg-white shadow-2xl z-40 flex flex-col">
            <div className="px-6 py-5 border-b border-slate-200 flex items-center justify-between">
              <h2 className="text-base font-bold text-slate-800">
                {drawer === "add" ? "Add New Teacher" : "Edit Teacher"}
              </h2>
              <button
                onClick={() => setDrawer(null)}
                className="p-1.5 hover:bg-slate-100 rounded-lg"
              >
                <X size={16} />
              </button>
            </div>
            <div className="flex-1 overflow-y-auto px-6 py-5 space-y-4">
              <Input
                label="Full Name"
                value={form.nama ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, nama: v }))}
                required
              />
              <Input
                label="Email"
                type="email"
                value={form.email ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, email: v }))}
                required
              />
              <Input
                label="Phone Number"
                value={form.no_hp ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, no_hp: v }))}
                required
              />
              <div className="pt-2 border-t border-slate-100">
                <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-3">
                  Expertise
                </p>
                <div className="space-y-3">
                  <Select
                    label="Subject"
                    value={formSubject}
                    onChange={setFormSubject}
                    options={Array.from(
                      new Map(MAPEL.map((m) => [m.nama, m])).values(),
                    ).map((m) => ({ value: m.nama, label: m.nama }))}
                    placeholder="Select subject"
                  />
                  <Select
                    label="Education Level"
                    value={formEduLevel}
                    onChange={setFormEduLevel}
                    options={JENJANG.map((j) => ({
                      value: j.nama,
                      label: j.nama,
                    }))}
                    placeholder="Select level"
                  />
                </div>
              </div>
            </div>
            <div className="px-6 py-4 border-t border-slate-200 flex gap-3">
              <Button
                variant="secondary"
                onClick={() => setDrawer(null)}
                className="flex-1 justify-center"
              >
                Cancel
              </Button>
              <Button onClick={handleSave} className="flex-1 justify-center">
                {drawer === "add" ? "Add Teacher" : "Save Changes"}
              </Button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

function AdminAdmins({ search }: { search: string }) {
  const [drawer, setDrawer] = useState<DrawerMode>(null);
  const [admins, setAdmins] = useState<Admin[]>(ADMINS);
  const [form, setForm] = useState<Partial<Admin>>({});

  const filtered = admins.filter((a) =>
    [a.nama, a.email, a.no_hp]
      .join(" ")
      .toLowerCase()
      .includes(search.toLowerCase()),
  );
  function handleSave() {
    setAdmins((p) => [...p, { ...form, id: Date.now() } as Admin]);
    setDrawer(null);
  }

  return (
    <div className="space-y-5 relative">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-800">Admins</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Manage admin accounts with platform access.
          </p>
        </div>
        <Button
          onClick={() => {
            setForm({});
            setDrawer("add");
          }}
          icon={<Plus size={15} />}
        >
          Add Admin
        </Button>
      </div>
      <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-slate-50 border-b border-slate-100">
              {["Name", "Email", "Phone", "Role"].map((h) => (
                <th
                  key={h}
                  className="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider"
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.map((a) => (
              <tr
                key={a.id}
                className="border-b border-slate-50 hover:bg-slate-50/80 transition-colors"
              >
                <td className="px-5 py-3.5">
                  <div className="flex items-center gap-2.5">
                    <Avatar name={a.nama} size="sm" />
                    <span className="font-medium text-slate-800">{a.nama}</span>
                  </div>
                </td>
                <td className="px-5 py-3.5 text-slate-600">{a.email}</td>
                <td className="px-5 py-3.5 text-slate-600">{a.no_hp}</td>
                <td className="px-5 py-3.5">
                  <Badge label="Administrator" variant="warning" />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {drawer && (
        <>
          <div
            className="fixed inset-0 bg-black/25 z-30"
            onClick={() => setDrawer(null)}
          />
          <div className="fixed top-0 right-0 h-full w-96 bg-white shadow-2xl z-40 flex flex-col">
            <div className="px-6 py-5 border-b border-slate-200 flex items-center justify-between">
              <h2 className="text-base font-bold text-slate-800">
                Add New Admin
              </h2>
              <button
                onClick={() => setDrawer(null)}
                className="p-1.5 hover:bg-slate-100 rounded-lg"
              >
                <X size={16} />
              </button>
            </div>
            <div className="flex-1 px-6 py-5 space-y-4">
              <Input
                label="Full Name"
                value={form.nama ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, nama: v }))}
                required
              />
              <Input
                label="Email"
                type="email"
                value={form.email ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, email: v }))}
                required
              />
              <Input
                label="Phone Number"
                value={form.no_hp ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, no_hp: v }))}
                required
              />
            </div>
            <div className="px-6 py-4 border-t border-slate-200 flex gap-3">
              <Button
                variant="secondary"
                onClick={() => setDrawer(null)}
                className="flex-1 justify-center"
              >
                Cancel
              </Button>
              <Button onClick={handleSave} className="flex-1 justify-center">
                Add Admin
              </Button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

// ─── Dashboard Summary Pages ──────────────────────────────────────────────
function StudentDashboard({
  setPage,
  loggedInName,
  loggedInId,
  activeLessons,
}: {
  setPage: (p: string) => void;
  loggedInName: string;
  loggedInId: number | null;
  activeLessons: Les[];
}) {
  const myLes = activeLessons;
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-800">
          Welcome back, {loggedInName || "Siswa"}!
        </h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Here is an overview of your tutoring activity.
        </p>
      </div>
      <div className="grid grid-cols-3 gap-5">
        {[
          {
            label: "Total Lessons",
            value: String(myLes.length),
            sub: "All time",
            color: "bg-indigo-50",
            iconColor: "text-[#4361EE]",
            Icon: BookOpen,
          },
          {
            label: "Upcoming",
            value: String(
              myLes.filter((l) => new Date(l.tanggal_mulai) > new Date())
                .length,
            ),
            sub: "Scheduled",
            color: "bg-sky-50",
            iconColor: "text-sky-500",
            Icon: Calendar,
          },
          {
            label: "Completed",
            value: String(
              myLes.filter((l) => new Date(l.tanggal_mulai) <= new Date())
                .length,
            ),
            sub: "Finished",
            color: "bg-emerald-50",
            iconColor: "text-emerald-500",
            Icon: CheckCircle2,
          },
        ].map(({ label, value, sub, color, iconColor, Icon }) => (
          <div
            key={label}
            className="bg-white border border-slate-200 rounded-xl p-5 flex items-center gap-4"
          >
            <div
              className={`w-11 h-11 ${color} rounded-xl flex items-center justify-center flex-shrink-0`}
            >
              <Icon size={20} className={iconColor} />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-800">{value}</p>
              <p className="text-xs text-slate-500">{label}</p>
            </div>
          </div>
        ))}
      </div>
      <div className="grid grid-cols-2 gap-5">
        <div className="bg-white border border-slate-200 rounded-xl p-5 space-y-3">
          <h2 className="text-sm font-semibold text-slate-700">
            Quick Actions
          </h2>
          <div className="space-y-2">
            <button
              onClick={() => setPage("book")}
              className="w-full flex items-center justify-between px-4 py-3 bg-indigo-50 hover:bg-indigo-100 rounded-xl transition-colors text-[#4361EE] font-medium text-sm"
            >
              <span className="flex items-center gap-2">
                <BookOpen size={16} />
                Book a New Lesson
              </span>
              <ChevronRight size={16} />
            </button>
            <button
              onClick={() => setPage("mylessons")}
              className="w-full flex items-center justify-between px-4 py-3 bg-slate-50 hover:bg-slate-100 rounded-xl transition-colors text-slate-700 font-medium text-sm"
            >
              <span className="flex items-center gap-2">
                <Calendar size={16} />
                View All My Lessons
              </span>
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
        <div className="bg-white border border-slate-200 rounded-xl p-5 space-y-3">
          <h2 className="text-sm font-semibold text-slate-700">Recent Lessons</h2>
          {myLes.slice(0, 2).map((les) => {
            const jadwal = JADWAL.find((j) => j.id === les.id_jadwal);
            const guru = GURU.find((g) => g.id === jadwal?.id_guru);
            const mapel = MAPEL.find((m) => m.id === les.id_mapel);

            // Gunakan fungsi formatDateEn yang sama dengan MyLessons agar bahasa Inggris
            const dateStr = les.tanggal_mulai
              ? new Date(les.tanggal_mulai).toLocaleDateString("en-US", {
                weekday: "short",
                day: "numeric",
                month: "long",
                year: "numeric",
              })
              : "-";

            return (
              <div key={les.id} className="flex items-center gap-3 p-3 border border-slate-100 rounded-xl">
                <Avatar name={guru?.nama ?? "?"} size="sm" />
                <div>
                  <p className="text-sm font-medium text-slate-700">{mapel?.nama}</p>
                  {/* Tampilan yang sudah bersih dan bahasa Inggris */}
                  <p className="text-xs text-slate-400">
                    {guru?.nama} · {dateStr}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

function TeacherDashboard({ loggedInId, loggedInName }: any) {
  const guruId = loggedInId;

  // --- TAMBAHKAN KEMBALI BARIS INI (YANG HILANG) ---
  const guruJadwalIds = JADWAL.filter((j) => j.id_guru === guruId).map((j) => j.id);
  // ------------------------------------------------

  // Baris ini akan otomatis normal kembali dan tidak merah lagi
  const myLessons = LES_DATA.filter((l) => guruJadwalIds.includes(l.id_jadwal));

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-800">
          Welcome, {loggedInName}!
        </h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Overview of your teaching schedule and students.
        </p>
      </div>
      {/* ... sisa kode ke bawah biarkan sama ... */}
      <div className="grid grid-cols-4 gap-5">
        {[
          {
            label: "Total Lessons",
            value: String(myLessons.length),
            Icon: BookOpen,
            color: "bg-indigo-50 text-[#4361EE]",
          },
          {
            label: "Availability Slots",
            value: String(JADWAL.filter((j) => j.id_guru === guruId).length),
            Icon: Clock,
            color: "bg-violet-50 text-violet-500",
          },
          {
            label: "Unique Students",
            value: String(new Set(myLessons.map((l) => l.id_siswa)).size),
            Icon: Users,
            color: "bg-sky-50 text-sky-500",
          },
          {
            label: "Expertise Areas",
            value: String(KEAHLIAN.filter((k) => k.id_guru === guruId).length),
            Icon: GraduationCap,
            color: "bg-emerald-50 text-emerald-500",
          },
        ].map(({ label, value, Icon, color }) => (
          <div
            key={label}
            className="bg-white border border-slate-200 rounded-xl p-5 flex items-center gap-4"
          >
            <div
              className={`w-11 h-11 rounded-xl flex items-center justify-center flex-shrink-0 ${color.split(" ")[0]}`}
            >
              <Icon size={20} className={color.split(" ")[1]} />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-800">{value}</p>
              <p className="text-xs text-slate-500">{label}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

function AdminDashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-800">Admin Dashboard</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Platform-wide overview of EduCapy.
        </p>
      </div>
      <div className="grid grid-cols-4 gap-5">
        {[
          {
            label: "Total Students",
            value: String(SISWA.length),
            Icon: GraduationCap,
            color: "bg-sky-50 text-sky-500",
          },
          {
            label: "Total Teachers",
            value: String(GURU.length),
            Icon: UserCheck,
            color: "bg-violet-50 text-violet-500",
          },
          {
            label: "Total Lessons",
            value: String(LES_DATA.length),
            Icon: BookOpen,
            color: "bg-indigo-50 text-[#4361EE]",
          },
          {
            label: "Admins",
            value: String(ADMINS.length),
            Icon: Shield,
            color: "bg-emerald-50 text-emerald-500",
          },
        ].map(({ label, value, Icon, color }) => (
          <div
            key={label}
            className="bg-white border border-slate-200 rounded-xl p-5 flex items-center gap-4"
          >
            <div
              className={`w-11 h-11 rounded-xl flex items-center justify-center flex-shrink-0 ${color.split(" ")[0]}`}
            >
              <Icon size={20} className={color.split(" ")[1]} />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-800">{value}</p>
              <p className="text-xs text-slate-500">{label}</p>
            </div>
          </div>
        ))}
      </div>
      <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
        <div className="px-5 py-3 border-b border-slate-100">
          <p className="text-sm font-semibold text-slate-700">
            Recent Lesson Bookings
          </p>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-slate-50 border-b border-slate-100">
              {["Student", "Teacher", "Subject", "Date", "Time"].map((h) => (
                <th
                  key={h}
                  className="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider"
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {LES_DATA.map((les) => {
              // FIX: guru lewat jadwal.id_guru, mapel lewat les.id_mapel
              const jadwal = JADWAL.find((j) => j.id === les.id_jadwal);
              const guru = GURU.find((g) => g.id === jadwal?.id_guru);
              const siswa = SISWA.find((s) => s.id === les.id_siswa);
              const mapel = MAPEL.find((m) => m.id === les.id_mapel);
              return (
                <tr
                  key={les.id}
                  className="border-b border-slate-50 hover:bg-slate-50/80 transition-colors"
                >
                  <td className="px-5 py-3.5">
                    <div className="flex items-center gap-2">
                      <Avatar name={siswa?.nama ?? "?"} size="sm" />
                      <span className="font-medium text-slate-800">
                        {siswa?.nama}
                      </span>
                    </div>
                  </td>
                  <td className="px-5 py-3.5 text-slate-600">{guru?.nama}</td>
                  <td className="px-5 py-3.5">
                    <Badge label={mapel?.nama ?? "—"} variant="info" />
                  </td>
                  <td className="px-5 py-3.5 text-slate-500">
                    {les.tanggal_mulai.split("T")[0]}
                  </td>
                  <td className="px-5 py-3.5 text-slate-500">
                    {toHHMM(les.tanggal_mulai)} – {toHHMM(les.tanggal_selesai)}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ─── Admin: Manage Schedules ──────────────────────────────────────────────
function AdminSchedules({ search }: { search: string }) {
  const [drawer, setDrawer] = useState<DrawerMode>(null);
  const [schedules, setSchedules] = useState<JadwalKesediaan[]>(JADWAL);
  // FIX: form tidak pakai id_keahlian, tapi id_guru
  const [form, setForm] = useState<Partial<JadwalKesediaan>>({});

  // FIX: enriched pakai jadwal.id_guru langsung
  const enriched = schedules
    .map((j) => {
      const guru = GURU.find((g) => g.id === j.id_guru);
      return { ...j, guru };
    })
    .filter((r) =>
      [r.guru?.nama, r.hari]
        .join(" ")
        .toLowerCase()
        .includes(search.toLowerCase()),
    );

  function handleDelete(id: number) {
    setSchedules((p) => p.filter((j) => j.id !== id));
  }
  function handleSave() {
    if (
      drawer === "add" &&
      form.hari &&
      form.jam_mulai &&
      form.jam_selesai &&
      form.id_guru
    ) {
      setSchedules((p) => [
        ...p,
        {
          id: Date.now(),
          id_guru: form.id_guru!,
          hari: form.hari!,
          jam_mulai: form.jam_mulai!,
          jam_selesai: form.jam_selesai!,
          status: "tersedia",
          id_admin: 1,
        },
      ]);
    }
    setDrawer(null);
  }

  return (
    <div className="space-y-5 relative">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-800">Manage Schedules</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            View and manage all teacher availability slots.
          </p>
        </div>
        <Button
          onClick={() => {
            setForm({});
            setDrawer("add");
          }}
          icon={<Plus size={15} />}
        >
          Add Schedule
        </Button>
      </div>
      <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
        <div className="px-5 py-3 border-b border-slate-100 flex items-center justify-between">
          <p className="text-sm text-slate-500">{enriched.length} schedules</p>
          <Button variant="ghost" size="sm" icon={<Download size={14} />}>
            Export
          </Button>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-slate-50 border-b border-slate-100">
              {["Teacher", "Day", "Time Slot", "Status", "Actions"].map((h) => (
                <th
                  key={h}
                  className="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider"
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {enriched.map((r) => (
              <tr
                key={r.id}
                className="border-b border-slate-50 hover:bg-slate-50/80 transition-colors group"
              >
                <td className="px-5 py-3.5">
                  <div className="flex items-center gap-2.5">
                    <Avatar name={r.guru?.nama ?? "?"} size="sm" />
                    <span className="font-medium text-slate-800">
                      {r.guru?.nama ?? "—"}
                    </span>
                  </div>
                </td>
                <td className="px-5 py-3.5 font-medium text-slate-700">
                  {r.hari}
                </td>
                <td className="px-5 py-3.5 text-slate-600 font-mono text-xs">
                  {r.jam_mulai} – {r.jam_selesai}
                </td>
                <td className="px-5 py-3.5">
                  <Badge
                    label={r.status}
                    variant={r.status === "tersedia" ? "success" : "warning"}
                  />
                </td>
                <td className="px-5 py-3.5">
                  <div className="opacity-0 group-hover:opacity-100 transition-opacity">
                    <button
                      onClick={() => handleDelete(r.id)}
                      className="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {drawer === "add" && (
        <>
          <div
            className="fixed inset-0 bg-black/25 z-30"
            onClick={() => setDrawer(null)}
          />
          <div className="fixed top-0 right-0 h-full w-96 bg-white shadow-2xl z-40 flex flex-col">
            <div className="px-6 py-5 border-b border-slate-200 flex items-center justify-between">
              <h2 className="text-base font-bold text-slate-800">
                Add Availability Slot
              </h2>
              <button
                onClick={() => setDrawer(null)}
                className="p-1.5 hover:bg-slate-100 rounded-lg"
              >
                <X size={16} />
              </button>
            </div>
            <div className="flex-1 px-6 py-5 space-y-4">
              {/* FIX: pilih guru langsung, tidak pakai keahlian */}
              <Select
                label="Teacher"
                value={String(form.id_guru ?? "")}
                onChange={(v) => setForm((p) => ({ ...p, id_guru: Number(v) }))}
                options={GURU.map((g) => ({
                  value: String(g.id),
                  label: g.nama,
                }))}
                placeholder="Select teacher"
                required
              />
              <Select
                label="Day"
                value={form.hari ?? ""}
                onChange={(v) => setForm((p) => ({ ...p, hari: v }))}
                options={HARI.map((h) => ({ value: h, label: h }))}
                placeholder="Select day"
                required
              />
              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="Start Time"
                  type="time"
                  value={form.jam_mulai ?? ""}
                  onChange={(v) => setForm((p) => ({ ...p, jam_mulai: v }))}
                  required
                />
                <Input
                  label="End Time"
                  type="time"
                  value={form.jam_selesai ?? ""}
                  onChange={(v) => setForm((p) => ({ ...p, jam_selesai: v }))}
                  required
                />
              </div>
            </div>
            <div className="px-6 py-4 border-t border-slate-200 flex gap-3">
              <Button
                variant="secondary"
                onClick={() => setDrawer(null)}
                className="flex-1 justify-center"
              >
                Cancel
              </Button>
              <Button onClick={handleSave} className="flex-1 justify-center">
                Add Schedule
              </Button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

// ─── Mock credentials ─────────────────────────────────────────────────────
const MOCK_ACCOUNTS = [
  {
    email: "rani@mail.com",
    password: "student123",
    role: "student" as Role,
    nama: "Rani Kusuma",
  },
  {
    email: "ayu@tutor.id",
    password: "teacher123",
    role: "teacher" as Role,
    nama: "Dr. Ayu Rahmawati",
  },
  {
    email: "reza@tutor.id",
    password: "admin123",
    role: "admin" as Role,
    nama: "Reza Firmansyah",
  },
];

// ─── Auth: Login Page ─────────────────────────────────────────────────────
function LoginPage({
  onLogin,
  onGoRegister,
}: {
  onLogin: (role: Role, nama: string, id: number) => void;
  onGoRegister: () => void;
}) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPwd, setShowPwd] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setLoading(true);

    const mockAccount = MOCK_ACCOUNTS.find(
      (a) => a.email === email && a.password === password,
    );

    if (mockAccount) {
      setTimeout(() => {
        onLogin(mockAccount.role, mockAccount.nama, 1);
        setLoading(false);
      }, 500);
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, password: password }),
      });

      const result = await response.json();

      if (result.status === "sukses") {
        onLogin(result.role as Role, result.nama, result.id);
      } else {
        setError(
          result.pesan || "Invalid email or password. Please try again.",
        );
      }
    } catch (err) {
      setError("Tidak bisa terhubung ke server. Pastikan Java sudah jalan.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div
      className="min-h-screen bg-[#F5F7FA] flex"
      style={{ fontFamily: "'Inter', sans-serif" }}
    >
      <div className="hidden lg:flex w-[480px] flex-shrink-0 bg-[#1C2B3A] flex-col justify-between p-12">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 bg-[#4361EE] rounded-xl flex items-center justify-center">
            <Layers size={18} className="text-white" />
          </div>
          <span className="text-white text-lg font-bold tracking-tight">
            EduCAPY
          </span>
        </div>
        <div className="space-y-6">
          <div className="space-y-3">
            <Badge label="Private Tutoring Management" variant="info" />
            <h2 className="text-3xl font-bold text-white leading-snug">
              The smarter way to manage private lessons.
            </h2>
            <p className="text-white/50 text-sm leading-relaxed">
              Book lessons, track schedules, and manage students and teachers —
              all in one place.
            </p>
          </div>
          <div className="grid grid-cols-2 gap-3">
            {[
              {
                icon: BookOpen,
                label: "Easy Booking",
                sub: "Step-by-step lesson booking",
              },
              {
                icon: Calendar,
                label: "Smart Scheduling",
                sub: "Overlap detection built-in",
              },
              {
                icon: Users,
                label: "Multi-role",
                sub: "Student, Teacher & Admin",
              },
              {
                icon: Shield,
                label: "Secure Access",
                sub: "Role-based permissions",
              },
            ].map(({ icon: Icon, label, sub }) => (
              <div
                key={label}
                className="bg-white/5 rounded-xl p-4 border border-white/10"
              >
                <Icon size={18} className="text-[#4361EE] mb-2" />
                <p className="text-white text-xs font-semibold">{label}</p>
                <p className="text-white/40 text-xs mt-0.5">{sub}</p>
              </div>
            ))}
          </div>
        </div>
        <p className="text-white/20 text-xs">
          © 2026 EduCAPY. All rights reserved.
        </p>
      </div>

      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-md space-y-8">
          <div className="flex items-center gap-2 lg:hidden">
            <div className="w-8 h-8 bg-[#4361EE] rounded-xl flex items-center justify-center">
              <Layers size={16} className="text-white" />
            </div>
            <span className="text-slate-800 text-base font-bold">EduCAPY</span>
          </div>

          <div>
            <h1 className="text-2xl font-bold text-slate-800">
              Sign in to your account
            </h1>
            <p className="text-sm text-slate-500 mt-1">
              {"Don't have an account? "}
              <button
                onClick={onGoRegister}
                className="text-[#4361EE] font-medium hover:underline underline-offset-2"
              >
                Register here
              </button>
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Email Address"
              type="email"
              value={email}
              onChange={setEmail}
              placeholder="you@example.com"
              required
            />
            <div className="flex flex-col gap-1">
              <label className="text-xs font-medium text-slate-600">
                Password <span className="text-red-400">*</span>
              </label>
              <div className="relative">
                <input
                  type={showPwd ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter your password"
                  required
                  className="w-full px-3 py-2 pr-10 text-sm border border-slate-200 rounded-lg bg-white text-slate-800 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#4361EE]/30 focus:border-[#4361EE] transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPwd((v) => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 text-xs font-medium"
                >
                  {showPwd ? "Hide" : "Show"}
                </button>
              </div>
            </div>
            {error && (
              <div className="flex items-center gap-2 text-sm text-red-600 bg-red-50 border border-red-200 rounded-xl px-4 py-3">
                <AlertTriangle size={15} className="flex-shrink-0" />
                {error}
              </div>
            )}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 bg-[#4361EE] hover:bg-[#3451DB] text-white text-sm font-semibold rounded-xl transition-all shadow-sm disabled:opacity-60 flex items-center justify-center gap-2"
            >
              {loading ? (
                <svg
                  className="animate-spin h-4 w-4 text-white"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  />
                  <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8v8z"
                  />
                </svg>
              ) : null}
              {loading ? "Signing in..." : "Sign In"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

// ─── Auth: Register Page ──────────────────────────────────────────────────
// ─── Auth: Register Page ──────────────────────────────────────────────────
function RegisterPage({ onGoLogin }: { onGoLogin: () => void }) {
  const [role, setRegRole] = useState<Role>("student");
  const [step, setStep] = useState<1 | 2>(1);
  const [form, setForm] = useState({ nama: "", email: "", no_hp: "", password: "", confirm: "", tanggal_lahir: "", jenis_kelamin: "", id_jenjang: "" });
  const [error, setError] = useState("");
  const [done, setDone] = useState(false);

  // State khusus multi-keahlian guru
  const [expertises, setExpertises] = useState<{ mapel: string, jenjang: string }[]>([]);
  const [tempMapel, setTempMapel] = useState("");
  const [tempJenjang, setTempJenjang] = useState("");

  function f(k: keyof typeof form) {
    return (v: string) => setForm((p) => ({ ...p, [k]: v }));
  }

  function handleStep1(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    if (form.password !== form.confirm) { setError("Passwords do not match."); return; }
    if (form.password.length < 6) { setError("Password must be at least 6 characters."); return; }
    setStep(2);
  }

  function addExpertise() {
    if (tempMapel && tempJenjang) {
      if (!expertises.some(e => e.mapel === tempMapel && e.jenjang === tempJenjang)) {
        setExpertises(prev => [...prev, { mapel: tempMapel, jenjang: tempJenjang }]);
      }
      setTempMapel("");
      setTempJenjang("");
    }
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError("");

    // Otomatis masukkan keahlian jika belum diklik "+" tapi tombol Submit keburu ditekan
    let finalExp = [...expertises];
    if (role === "teacher" && tempMapel && tempJenjang) {
      if (!finalExp.some(exp => exp.mapel === tempMapel && exp.jenjang === tempJenjang)) {
        finalExp.push({ mapel: tempMapel, jenjang: tempJenjang });
      }
    }

    const dataRegistrasi = {
      role: role,
      nama: form.nama,
      email: form.email,
      password: form.password,
      no_hp: form.no_hp,
      tanggal_lahir: form.tanggal_lahir,
      jenis_kelamin: form.jenis_kelamin,
      id_jenjang: form.id_jenjang,
      expertises: finalExp.map(exp => `${exp.mapel}-${exp.jenjang}`).join(",") // Gabung jadi string
    };

    try {
      const response = await fetch("http://localhost:8080/api/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dataRegistrasi),
      });

      const result = await response.json();

      if (result.status === "sukses") {
        setDone(true);
      } else {
        setError("Gagal menyimpan ke database: " + result.pesan);
      }
    } catch (err) {
      setError("Tidak bisa terhubung ke server. Pastikan Java sudah jalan.");
    }
  }

  const roleColors: Record<Role, string> = {
    student: "border-sky-400 bg-sky-50 text-sky-700",
    teacher: "border-violet-400 bg-violet-50 text-violet-700",
    admin: "border-indigo-400 bg-indigo-50 text-indigo-700",
  };

  if (done) {
    return (
      <div className="min-h-screen bg-[#F5F7FA] flex items-center justify-center" style={{ fontFamily: "'Inter', sans-serif" }}>
        <div className="text-center space-y-5 max-w-sm">
          <div className="w-16 h-16 bg-emerald-100 rounded-full flex items-center justify-center mx-auto">
            <CheckCircle2 size={32} className="text-emerald-500" />
          </div>
          <div>
            <h2 className="text-xl font-bold text-slate-800">Account Created!</h2>
            <p className="text-sm text-slate-500 mt-1">Your {role} account for <strong>{form.email}</strong> has been registered.</p>
          </div>
          <Button onClick={onGoLogin} className="mx-auto">Go to Sign In</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#F5F7FA] flex" style={{ fontFamily: "'Inter', sans-serif" }}>
      <div className="hidden lg:flex w-[480px] flex-shrink-0 bg-[#1C2B3A] flex-col justify-between p-12">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 bg-[#4361EE] rounded-xl flex items-center justify-center"><Layers size={18} className="text-white" /></div>
          <span className="text-white text-lg font-bold">EduCAPY</span>
        </div>
        <div className="space-y-5">
          <h2 className="text-3xl font-bold text-white leading-snug">Join EduCAPY today.</h2>
          <p className="text-white/50 text-sm leading-relaxed">Create your account and start managing your learning or teaching journey with ease.</p>
          <div className="space-y-3">
            {[
              { step: "1", label: "Choose your role", sub: "Student, Teacher, or Admin" },
              { step: "2", label: "Fill in your details", sub: "Basic info and credentials" },
              { step: "3", label: "Start using Educapy", sub: "Book lessons or manage schedules" },
            ].map(({ step: s, label, sub }) => (
              <div key={s} className="flex items-start gap-3">
                <div className="w-6 h-6 bg-[#4361EE]/20 border border-[#4361EE]/30 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5"><span className="text-xs font-bold text-[#4361EE]">{s}</span></div>
                <div><p className="text-white text-sm font-semibold">{label}</p><p className="text-white/40 text-xs">{sub}</p></div>
              </div>
            ))}
          </div>
        </div>
        <p className="text-white/20 text-xs">© 2026 Educapy. All rights reserved.</p>
      </div>

      <div className="flex-1 flex items-center justify-center p-8 overflow-y-auto">
        <div className="w-full max-w-md space-y-7">
          <div>
            <h1 className="text-2xl font-bold text-slate-800">Create your account</h1>
            <p className="text-sm text-slate-500 mt-1">Already have an account? <button onClick={onGoLogin} className="text-[#4361EE] font-medium hover:underline underline-offset-2">Sign in</button></p>
          </div>

          <div className="flex items-center gap-3">
            {["Account Type & Credentials", "Profile Details"].map((s, i) => (
              <div key={s} className="flex items-center gap-2 flex-1">
                <div className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 ${step > i + 1 ? "bg-emerald-500 text-white" : step === i + 1 ? "bg-[#4361EE] text-white" : "bg-slate-200 text-slate-400"}`}>{step > i + 1 ? <Check size={11} /> : i + 1}</div>
                <span className={`text-xs font-medium hidden sm:block ${step === i + 1 ? "text-[#4361EE]" : "text-slate-400"}`}>{s}</span>
                {i === 0 && <div className={`flex-1 h-px ${step > 1 ? "bg-emerald-400" : "bg-slate-200"}`} />}
              </div>
            ))}
          </div>

          {step === 1 && (
            <form onSubmit={handleStep1} className="space-y-5">
              <div className="space-y-2">
                <label className="text-xs font-medium text-slate-600">Register as <span className="text-red-400">*</span></label>
                <div className="grid grid-cols-3 gap-2">
                  {(["student", "teacher", "admin"] as Role[]).map((r) => {
                    const icons = { student: GraduationCap, teacher: UserCheck, admin: Shield };
                    const Icon = icons[r];
                    return (
                      <button key={r} type="button" onClick={() => setRegRole(r)} className={`flex flex-col items-center gap-1.5 py-3 px-2 rounded-xl border-2 transition-all ${role === r ? roleColors[r] + " border-current" : "border-slate-200 text-slate-500 hover:border-slate-300 hover:bg-slate-50"}`}>
                        <Icon size={18} /><span className="text-xs font-semibold capitalize">{r}</span>
                      </button>
                    );
                  })}
                </div>
              </div>
              <Input label="Full Name" value={form.nama} onChange={f("nama")} placeholder="Your full name" required />
              <Input label="Email Address" type="email" value={form.email} onChange={f("email")} placeholder="you@example.com" required />
              <div className="grid grid-cols-2 gap-3">
                <Input label="Password" type="password" value={form.password} onChange={f("password")} placeholder="Min. 6 characters" required />
                <Input label="Confirm Password" type="password" value={form.confirm} onChange={f("confirm")} placeholder="Repeat password" required />
              </div>
              {error && <div className="flex items-center gap-2 text-sm text-red-600 bg-red-50 border border-red-200 rounded-xl px-4 py-3"><AlertTriangle size={15} className="flex-shrink-0" />{error}</div>}
              <button type="submit" className="w-full py-2.5 bg-[#4361EE] hover:bg-[#3451DB] text-white text-sm font-semibold rounded-xl transition-all shadow-sm">Continue</button>
            </form>
          )}

          {step === 2 && (
            <form onSubmit={handleSubmit} className="space-y-4">
              <Input label="Phone Number" value={form.no_hp} onChange={f("no_hp")} placeholder="e.g. 08123456789" required />
              {role === "student" && (
                <>
                  <Input label="Date of Birth" type="date" value={form.tanggal_lahir} onChange={f("tanggal_lahir")} required />
                  <Select label="Gender" value={form.jenis_kelamin} onChange={f("jenis_kelamin")} options={[{ value: "L", label: "Male" }, { value: "P", label: "Female" }]} placeholder="Select gender" required />
                  <Select label="Education Level" value={form.id_jenjang} onChange={f("id_jenjang")} options={JENJANG.map((j) => ({ value: String(j.id), label: j.nama }))} placeholder="Select level" required />
                </>
              )}
              {role === "teacher" && (
                <div className="pt-2 border-t border-slate-100">
                  <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-3">Expertise (Bisa Tambah &gt; 1)</p>
                  <div className="space-y-3">
                    <div className="flex gap-2 items-end">
                      <div className="flex-1">
                        <Select label="Subject" value={tempMapel} onChange={setTempMapel} options={MAPEL.map((m) => ({ value: String(m.id), label: m.nama }))} placeholder="Select subject" />
                      </div>
                      <div className="flex-1">
                        <Select label="Education Level" value={tempJenjang} onChange={setTempJenjang} options={JENJANG.map((j) => ({ value: String(j.id), label: j.nama }))} placeholder="Select level" />
                      </div>
                      <Button type="button" onClick={addExpertise} size="md" className="mb-0.5" variant="primary"><Plus size={16} /></Button>
                    </div>

                    {expertises.length > 0 && (
                      <div className="flex flex-wrap gap-2 p-3 bg-slate-50 rounded-lg border border-slate-100">
                        {expertises.map((e, idx) => {
                          const mName = MAPEL.find(m => String(m.id) === e.mapel)?.nama;
                          const jName = JENJANG.find(j => String(j.id) === e.jenjang)?.nama;
                          return (
                            <span key={idx} className="inline-flex items-center gap-1.5 px-2.5 py-1 bg-white border border-slate-200 rounded-md text-xs font-medium text-slate-600 shadow-sm">
                              {mName} ({jName})
                              <button type="button" onClick={() => setExpertises(expertises.filter((_, i) => i !== idx))} className="text-red-400 hover:text-red-600"><X size={12} /></button>
                            </span>
                          );
                        })}
                      </div>
                    )}
                  </div>
                </div>
              )}
              <div className="flex gap-3 pt-1">
                <Button type="button" variant="secondary" onClick={() => setStep(1)} className="flex-1 justify-center">Back</Button>
                <button type="submit" className="flex-1 py-2 bg-[#4361EE] hover:bg-[#3451DB] text-white text-sm font-semibold rounded-xl transition-all shadow-sm">Create Account</button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

// ─── Main App ─────────────────────────────────────────────────────────────
export default function App() {
  const [authScreen, setAuthScreen] = useState<"login" | "register" | null>(
    "login",
  );
  const [loggedInRole, setLoggedInRole] = useState<Role>("student");
  const [loggedInName, setLoggedInName] = useState("");
  const [page, setPage] = useState("dashboard");
  const [globalSearch, setGlobalSearch] = useState("");
  const [loggedInId, setLoggedInId] = useState<number | null>(null);
  const [activeLessons, setActiveLessons] = useState<Les[]>([]);

  useEffect(() => {
    if (loggedInId) {
      fetch(`http://localhost:8080/api/les/siswa?id_siswa=${loggedInId}`)
        .then((res) => res.json())
        .then((data) => setActiveLessons(data))
        .catch((err) => console.error("Gagal mengambil data les:", err));
    }
  }, [loggedInId, page]);

  function handleLogin(role: Role, nama: string, id: number) {
    setLoggedInRole(role);
    setLoggedInName(nama);
    setLoggedInId(id);
    setAuthScreen(null);
    setPage("dashboard");
  }

  function handleLogout() {
    setAuthScreen("login");
    setPage("dashboard");
    setGlobalSearch("");
  }

  function handleRoleSwitch(r: Role) {
    setLoggedInRole(r);
    setPage("dashboard");
  }

  if (authScreen === "login")
    return (
      <LoginPage
        onLogin={handleLogin}
        onGoRegister={() => setAuthScreen("register")}
      />
    );
  if (authScreen === "register")
    return <RegisterPage onGoLogin={() => setAuthScreen("login")} />;

  function renderPage() {
    if (loggedInRole === "student") {
      if (page === "book")
        return (
          <BookLesson
            loggedInId={loggedInId}
            setActiveLessons={setActiveLessons}
          />
        );
      if (page === "mylessons")
        return (
          <MyLessons loggedInId={loggedInId} activeLessons={activeLessons} />
        );
      return (
        <StudentDashboard
          setPage={setPage}
          loggedInName={loggedInName}
          loggedInId={loggedInId}
          activeLessons={activeLessons}
        />
      );
    }
    if (loggedInRole === "teacher") {
      // Tambahkan pelemparan parameter loggedInId dan loggedInName di sini
      if (page === "availability") return <TeacherAvailability loggedInId={loggedInId} />;
      if (page === "schedule") return <TeacherSchedule loggedInId={loggedInId} />;
      return <TeacherDashboard loggedInId={loggedInId} loggedInName={loggedInName} />;
    }
    if (page === "students") return <AdminStudents search={globalSearch} />;
    if (page === "teachers") return <AdminTeachers search={globalSearch} />;
    if (page === "admins") return <AdminAdmins search={globalSearch} />;
    if (page === "schedules") return <AdminSchedules search={globalSearch} />;
    return <AdminDashboard />;
  }

  return (
    <div
      className="min-h-screen bg-[#F5F7FA]"
      style={{ fontFamily: "'Inter', sans-serif" }}
    >
      <Sidebar role={loggedInRole} page={page} setPage={setPage} />
      <div className="pl-56 flex flex-col min-h-screen">
        <TopBar
          role={loggedInRole}
          setRole={handleRoleSwitch}
          globalSearch={globalSearch}
          setGlobalSearch={setGlobalSearch}
          onLogout={handleLogout}
          loggedInName={loggedInName}
        />
        <main className="flex-1 p-8 max-w-[1280px] w-full">{renderPage()}</main>
      </div>
    </div>
  );
}
