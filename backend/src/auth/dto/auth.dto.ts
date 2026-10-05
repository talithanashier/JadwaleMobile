export class LoginDto {
  email!: string;
  password!: string;
}

export class SignupSchoolDto {
  nama!: string;
  email!: string;
  password!: string;
  nama_sekolah!: string;
  npsn?: string;
}

export class SignupTeacherDto {
  nama!: string;
  email!: string;
  password!: string;
  id_sekolah!: number;
  nip?: string;
}

export class SignupPublicDto {
  nama!: string;
  email!: string;
  password!: string;
}

export class UpdateProfileDto {
  nama?: string;
  email?: string;
  no_hp?: string;
  password?: string;
}

export class ForgotPasswordDto {
  email!: string;
}

export class ResetPasswordDto {
  token!: string;
  new_password?: string;
  password?: string;
}
